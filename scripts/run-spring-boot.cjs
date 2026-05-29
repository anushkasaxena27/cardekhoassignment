const { spawn } = require('child_process');
const path = require('path');

const root = path.resolve(__dirname, '..');
const isWin = process.platform === 'win32';
const mvnw = isWin ? 'mvnw.cmd' : './mvnw';

const child = spawn(mvnw, ['spring-boot:run'], {
  cwd: root,
  stdio: 'inherit',
  env: { ...process.env, SERVER_PORT: process.env.SERVER_PORT || '8081' },
  shell: isWin,
});

child.on('error', (err) => {
  console.error(err);
  process.exit(1);
});

child.on('exit', (code, signal) => {
  if (signal) {
    process.exit(1);
  }
  process.exit(code ?? 0);
});
