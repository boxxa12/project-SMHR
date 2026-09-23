'use strict';
const fs = require('fs');
const path = require('path');
const vm = require('vm');
const assert = require('assert/strict');
const root = path.resolve(__dirname, '..');
const manifest = JSON.parse(fs.readFileSync(path.join(root, 'package2_manifest.json'), 'utf8'));
const folder = path.join(root, 'kubejs/server_scripts/progression');
const eras = fs.readdirSync(path.join(folder, 'eras')).filter(f=>f.endsWith('.js')).sort();
const scripts = ['package2_validation.js', ...eras.map(f=>'eras/'+f)].map(name=>({
  name, text:fs.readFileSync(path.join(folder,name),'utf8')
}));
for (const file of scripts) new vm.Script(file.text, {filename:file.name});
assert.equal(eras.length, 9);
assert.equal(scripts.length, 10);
const locationPattern = /^[a-z0-9_.-]+:[a-z0-9_./-]+$/;
const allRecipes = manifest.recipes.flatMap(g=>g.ids.map(id=>({...g,id})));
assert.equal(allRecipes.length, 9348);
assert.equal(new Set(allRecipes.map(r=>r.id)).size,9348);
assert.equal(manifest.items.length,118);
assert.equal(new Set(manifest.items.map(r=>r.item)).size,118);
assert.equal(new Set([...manifest.items.map(r=>r.id),...manifest.recipes.filter(r=>r.layer==='astages').map(r=>r.restrictionId)]).size,120);
assert(allRecipes.every(r=>locationPattern.test(r.id)&&locationPattern.test(r.type)));
assert(manifest.items.every(r=>locationPattern.test(r.item)));
assert.equal(manifest.items.filter(r=>r.disable).length,97);
assert.equal(manifest.items.filter(r=>r.optional).length,2);
assert.deepEqual(manifest.recipes.filter(r=>r.layer==='astages').map(r=>[r.type,...r.ids]),[
 ['minecraft:crafting','create:crafting/curiosities/item_copying'],
 ['minecraft:crafting','create:crafting/curiosities/toolbox_dyeing']
]);
const rmsGroups=manifest.recipes.filter(r=>r.layer==='rms');
assert.equal(rmsGroups.length,104);
assert.equal(rmsGroups.reduce((n,r)=>n+r.ids.length,0),9346);
assert.equal(rmsGroups.filter(r=>r.ids.length>1).length,101);
assert.equal(rmsGroups.filter(r=>r.ids.length===1).length,3);

function environment(options={}) {
 const calls={items:[],recipes:[],rms:[]}, logs=[], callbacks=[];
 const knownItems=new Set(manifest.items.map(r=>r.item));
 const knownTypes=new Set(manifest.recipes.map(r=>r.type));
 const knownRecipes=new Map(allRecipes.map(r=>[r.id,r.type]));
 const registered=new Map();
 if(options.missingItem) knownItems.delete(options.missingItem);
 if(options.missingType) knownTypes.delete(options.missingType);
 if(options.missingRecipe) knownRecipes.delete(options.missingRecipe);
 if(options.wrongType) knownRecipes.set(options.wrongType,'minecraft:smelting');
 const global={};
 const AStages={
  addRestrictionForItem(id,stage,item) {
   assert(locationPattern.test(item));
   const row={id,stage,item,disable:false};calls.items.push(row);
   return {disableBlockInteraction(){row.disable=true;return this}};
  },
  addRestrictionForRecipe(id,stage,type,recipe) {
   assert.equal(type,'minecraft:crafting');assert(locationPattern.test(recipe));
   calls.recipes.push({id,stage,type,recipe});
  }
 };
 function add(type,ids,stage,method) {
   ids=Array.from(ids);ids.forEach(id=>assert(locationPattern.test(id)));
   calls.rms.push({type,ids,stage,method});
   if(!knownTypes.has(type)) return;
   ids.forEach(id=>registered.set(type+'|'+id,{stage}));
 }
 const ctx=vm.createContext({global,console:{log:s=>logs.push(String(s)),error:s=>logs.push(String(s))},
  AStages,RecipeMachineStage:{addRecipe:(t,id,s)=>add(t,[id],s,'addRecipe'),addRecipes:(t,ids,s)=>add(t,ids,s,'addRecipes')},
  ServerEvents:{loaded:fn=>callbacks.push(fn)},
  Java:{loadClass(name){
    if(options.validatorException) throw new Error('simulated Java binding error');
    if(name==='net.minecraft.resources.ResourceLocation') return class {constructor(id){this.id=id}};
    if(name==='net.minecraftforge.registries.ForgeRegistries') return {
      ITEMS:{containsKey:rl=>knownItems.has(rl.id)},
      RECIPE_TYPES:{getValue:rl=>knownTypes.has(rl.id)?rl.id:null,getKey:t=>t}
    };
    if(name==='net.sdm.recipemachinestage.api.stage.StageContainer') return {getRecipeData:(t,id)=>registered.get(t+'|'+id.id)||null};
    throw new Error('Unexpected Java class '+name);
  }}
 });
 const package1={name:'stage_manifest.js',text:'global.ProgressionStages = {isValidStage: function(s) {return /^era_[1-9]$/.test(s)}}'};
 const sorted=[...scripts,...(options.missingManifest?[]:[package1])].sort((a,b)=>{
   const priority=s=>Number((s.text.match(/^\/\/ priority: (-?\d+)/)||[])[1]||0);
   return priority(b)-priority(a);
 });
 const loadErrors=[];
 sorted.forEach(file=>{
   if(options.skipEra && file.name.includes(options.skipEra)) return;
   try {vm.runInContext(file.text,ctx,{filename:file.name,timeout:3000})}
   catch(error) {loadErrors.push({file:file.name,message:error.message})}
 });
 if(options.missingRms) registered.delete('create:milling|'+options.missingRms);
 if(options.wrongStage) registered.set('create:milling|'+options.wrongStage,{stage:'era_9'});
 function validate() {
   callbacks.forEach(fn=>fn({server:{getRecipeManager:()=>({byKey:rl=>({
     isPresent:()=>knownRecipes.has(rl.id),get:()=>({getType:()=>knownRecipes.get(rl.id)})
   })})}}));
 }
 validate();
 return {calls,logs,global,loadErrors,ctx,validate};
}

const happy=environment();
assert.deepEqual(happy.loadErrors,[]);
assert.equal(happy.global.Package2EraGatingStatus.validation,'DATA_CHECK_PASSED');
assert.equal(happy.calls.items.length,118);
assert.equal(happy.calls.rms.length,104);
assert.equal(happy.calls.recipes.length,2);
assert.deepEqual(happy.calls.items,manifest.items.map(({id,stage,item,disable})=>({id,stage,item,disable})));
assert.deepEqual(happy.calls.rms.map(({type,ids,stage})=>({type,ids,stage})),rmsGroups.map(({type,ids,stage})=>({type,ids,stage})));
assert(!('ProgressionStages' in happy.ctx));
assert.equal(new Set(happy.global.Package2EraGatingStatus.completed).size,9);
const scenarios=[
 [{missingManifest:true},'Missing global.ProgressionStages'],
 [{skipEra:'era_04'},'Era script did not finish: era_4'],
 [{missingItem:'ae2:controller'},'Unknown item: ae2:controller'],
 [{missingType:'mekanism:enriching'},'Unknown RecipeType: mekanism:enriching'],
 [{missingRecipe:'create:milling/fern'},'Unknown recipe: create:milling/fern'],
 [{wrongType:'create:milling/fern'},'Wrong RecipeType: create:milling/fern'],
 [{missingRms:'create:milling/fern'},'Missing/wrong RMS registration'],
 [{wrongStage:'create:milling/fern'},'Missing/wrong RMS registration'],
 [{validatorException:true},'Validator exception']
];
for(const [options,needle] of scenarios) {
 const r=environment(options);
 assert.equal(r.global.Package2EraGatingStatus.validation,'FAILED',JSON.stringify(options));
 assert([...r.logs,...r.loadErrors.map(e=>e.message)].some(s=>s.includes(needle)),needle);
 assert(!r.logs.some(s=>s.includes('DATA CHECK PASSED')));
 if(options.missingManifest) assert.equal(r.calls.items.length,0);
}
// A fresh load must not retain completion/success from an earlier generation.
vm.runInContext(scripts[0].text,happy.ctx);
assert.equal(happy.global.Package2EraGatingStatus.validation,'NOT_RUN');
assert.equal(happy.global.Package2EraGatingStatus.completed.length,0);
const summary={syntaxFiles:10,items:118,interactionFlags:97,astagesRecipes:2,rmsCalls:104,rmsRecipeIds:9346,totalRecipeIds:9348,
 tests:['priority ordering with Package 1 global only','exact emitted registration arguments','qualified unique IDs','correct Create crafting route','optional locks remain enabled','fresh-load reset',...scenarios.map(([o])=>Object.keys(o)[0])],
 note:'PASS in Node with simulated bindings. Not a Minecraft/KubeJS/Rhino or multiplayer runtime verification.'};
fs.writeFileSync(path.join(root,'verification.json'),JSON.stringify(summary,null,2)+'\n');
console.log(JSON.stringify(summary,null,2));
