import { spawnSync } from 'node:child_process'
import { readFileSync } from 'node:fs'
import { dirname, resolve } from 'node:path'
import { fileURLToPath } from 'node:url'

const scriptDirectory = dirname(fileURLToPath(import.meta.url))
const projectRoot = resolve(scriptDirectory, '..')
const baselinePath = resolve(projectRoot, '.kiro/baseline/2026-08-25/quality-baseline.json')
const baseline = JSON.parse(readFileSync(baselinePath, 'utf8')).frontend.eslint

const eslintResult = spawnSync(
  resolve(projectRoot, 'node_modules/.bin/eslint'),
  ['.', '--ext', 'js,jsx', '--format', 'json'],
  {
    cwd: projectRoot,
    encoding: 'utf8',
    maxBuffer: 64 * 1024 * 1024
  }
)

if (eslintResult.error || !eslintResult.stdout) {
  console.error('无法执行 ESLint 基线检查。', eslintResult.error || eslintResult.stderr)
  process.exit(2)
}

let results
try {
  results = JSON.parse(eslintResult.stdout)
} catch (error) {
  console.error('无法解析 ESLint JSON 输出。', error.message)
  process.exit(2)
}

const actual = results.reduce(
  (summary, result) => ({
    errors: summary.errors + result.errorCount,
    warnings: summary.warnings + result.warningCount
  }),
  { errors: 0, warnings: 0 }
)

console.log(`ESLint 基线：${actual.errors} errors / ${actual.warnings} warnings`)
console.log(`允许上限：${baseline.errors} errors / ${baseline.warnings} warnings`)

if (actual.errors > baseline.errors || actual.warnings > baseline.warnings) {
  console.error('ESLint 技术债高于冻结基线，拒绝继续。')
  process.exit(1)
}

console.log('ESLint 未新增技术债。')
