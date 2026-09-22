const { app, BrowserWindow } = require('electron');
const { spawn } = require('child_process');
const http = require('http');
const path = require('path');

let mainWindow;
let javaProcess;

function createWindow() {
    mainWindow = new BrowserWindow({
        width: 1200,
        height: 800,
        autoHideMenuBar: true,
        fullscreenable: true,
        title: "Homeware POS",
        webPreferences: {
            nodeIntegration: false,
            contextIsolation: true
        }
    });

    mainWindow.maximize();

    // Wait for the Spring Boot server to become ready on port 9090
    const checkServerReady = setInterval(() => {
        http.get('http://localhost:9090/', (res) => {
            if (res.statusCode === 200 || res.statusCode === 404) {
                clearInterval(checkServerReady);
                mainWindow.loadURL('http://localhost:9090/');
            }
        }).on('error', (err) => {
            // Server not ready yet, keep waiting...
        });
    }, 1000);

    // Load a beautiful loading screen while waiting for Java to boot
    mainWindow.loadURL(`data:text/html;charset=utf-8,
    <html>
      <body style="display:flex;justify-content:center;align-items:center;height:100vh;background:%230f172a;color:white;font-family:sans-serif;">
        <div style="text-align:center;">
          <h1>Homeware POS Terminal</h1>
          <p style="color:%2338bdf8;">Starting database and background services...</p>
        </div>
      </body>
    </html>
  `);

    mainWindow.on('closed', function () {
        mainWindow = null;
    });
}

app.on('ready', () => {
    // In production, the jar will be distributed next to the resources folder via extraFiles
    const prodJarPath = path.join(process.resourcesPath, '..', 'pos-backend-0.0.1-SNAPSHOT.jar');
    const devJarPath = path.join(__dirname, '..', 'target', 'pos-backend-0.0.1-SNAPSHOT.jar');

    const jarToExecute = app.isPackaged ? prodJarPath : devJarPath;

    // Start Java process hidden in the background
    try {
        javaProcess = spawn('java', ['-jar', jarToExecute], {
            detached: false,
            stdio: 'ignore'
        });
    } catch (e) {
        console.error("Failed to start Java backend: " + e);
    }

    createWindow();
});

// Kill the Java backend when the Electron App closes
app.on('window-all-closed', function () {
    if (javaProcess) {
        javaProcess.kill();
    }
    if (process.platform !== 'darwin') {
        app.quit();
    }
});
