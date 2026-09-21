import json, pathlib, re, shutil

ROOT = pathlib.Path(__file__).resolve().parent
# In the delivered package, this script lives under tools/.
PACKAGE = ROOT.parent if ROOT.name == 'tools' else ROOT / 'package2_fixed'
MANIFEST = PACKAGE / 'package2_manifest.json'

def build():
    data = json.loads(MANIFEST.read_text(encoding='utf-8'))
    dest = PACKAGE / 'kubejs/server_scripts/progression/eras'
    dest.mkdir(parents=True, exist_ok=True)
    q = lambda value: json.dumps(value, ensure_ascii=False)
    for number in range(1, 10):
        stage = f'era_{number}'
        source = next(r['file'] for r in data['items'] if r['stage'] == stage)
        lines = [
            '// priority: -100',
            f'// Package 2 revision 1: {stage}. Minecraft Forge 1.20.1, AStages 2.5.3, RMS 2.4.0.',
            '// Package 1 stage_manifest.js must use priority 0 (default) or any value greater than -100.',
            '// Recipe IDs reconstructed from the original namespace/path export; server data validation required.',
            '// No stage grants. RMS integration/ownership and multiplayer enforcement remain runtime tests.',
            '// See package2_era_gating_fixed.md for scope, sources, installation and unresolved coverage.',
            '',
            'if (!global.ProgressionStages || typeof global.ProgressionStages.isValidStage !== "function") {',
            '  throw new Error("[package2] Missing global.ProgressionStages. Load Package 1 manifest at priority > -100.")',
            '}',
            f'if (!global.ProgressionStages.isValidStage({q(stage)})) {{',
            f'  throw new Error("[package2] Package 1 does not define {stage}.")',
            '}',
            'if (!global.Package2EraGatingStatus) {',
            '  throw new Error("[package2] Missing package2_validation.js; install the full revised package.")',
            '}',
            'if (typeof AStages === "undefined") {',
            '  throw new Error("[package2] AStages binding is unavailable.")',
            '}',
        ]
        groups = [r for r in data['recipes'] if r['stage'] == stage]
        if any(r['layer']=='rms' for r in groups):
            lines += ['if (typeof RecipeMachineStage === "undefined") {',
                      '  throw new Error("[package2] Legacy RecipeMachineStage binding is unavailable.")', '}']
        lines += ['', '// Item restrictions. Previously optional entries remain ENABLED, matching the supplied package.']
        items = [r for r in data['items'] if r['stage']==stage]
        for r in items:
            if r.get('optional'):
                lines.append('// Design-optional, currently enabled: ' + r['item'])
            lines.append(f'AStages.addRestrictionForItem({q(r["id"])}, {q(stage)}, {q(r["item"])})' + ('.disableBlockInteraction()' if r['disable'] else ''))
        lines += ['', '// Explicit recipe lists. Counts are registration requests, not proof of enforcement.']
        for r in groups:
            if r['layer']=='astages':
                lines += ['// Special Create crafting serializer; runtime RecipeType is minecraft:crafting.',
                          f'AStages.addRestrictionForRecipe({q(r["restrictionId"])}, {q(stage)}, {q(r["type"])}, {q(r["ids"][0])})']
            elif len(r['ids'])==1:
                lines.append(f'RecipeMachineStage.addRecipe({q(r["type"])}, {q(r["ids"][0])}, {q(stage)})')
            else:
                lines += [f'// {r["type"]}: {len(r["ids"])} candidate IDs; coverage requires live verification.',
                          f'RecipeMachineStage.addRecipes({q(r["type"])}, [']
                lines += ['  '+q(id)+(',' if i<len(r['ids'])-1 else '') for i,id in enumerate(r['ids'])]
                lines += [f'], {q(stage)})']
        lines += ['', f'global.Package2EraGatingStatus.completed.push({q(stage)})',
                  f'console.log("[package2] {stage}: registration calls issued; awaiting startup data checks. Runtime enforcement not verified.")', '']
        (dest/source).write_text('\n'.join(lines), encoding='utf-8')
    template = (ROOT/'runtime_validation.template.js').read_text(encoding='utf-8')
    validation_data = {'items':[{'item':r['item'],'stage':r['stage']} for r in data['items']],
                       'recipes':[{k:r[k] for k in ('type','ids','stage','layer')} for r in data['recipes']]}
    # Keep diagnostic JS small in executable complexity; avoid a 9,348-entry
    # object literal inside one Rhino/JVM function and oversized string constants.
    payload = json.dumps(validation_data, ensure_ascii=False, separators=(',',':'))
    chunks = [payload[i:i+4096] for i in range(0, len(payload), 4096)]
    expression = 'JSON.parse([\n' + ',\n'.join('    '+json.dumps(chunk) for chunk in chunks) + '\n  ].join(""))'
    text = template.replace('__MANIFEST__', expression)
    (dest.parent/'package2_validation.js').write_text(text, encoding='utf-8')
    print('Generated nine era scripts and startup validator.')

if __name__ == '__main__':
    build()
