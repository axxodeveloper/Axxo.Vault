package com.example.server

object PortalHtmlProvider {
    fun getPortalHtml(ip: String, port: Int): String {
        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Axxo Vault</title>
            <script src="https://cdn.tailwindcss.com"></script>
            <script>
                tailwind.config = {
                    theme: {
                        extend: {
                            colors: {
                                cyber: {
                                    900: '#060B12',
                                    800: '#0A121F',
                                    700: '#0F192A',
                                    glow: '#00D2FF',
                                    accent: '#10B981'
                                }
                            },
                            animation: {
                                'blob': 'blob 7s infinite',
                                'pulse-slow': 'pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite',
                                'float': 'float 6s ease-in-out infinite',
                            },
                            keyframes: {
                                blob: {
                                    '0%': { transform: 'translate(0px, 0px) scale(1)' },
                                    '33%': { transform: 'translate(30px, -50px) scale(1.1)' },
                                    '66%': { transform: 'translate(-20px, 20px) scale(0.9)' },
                                    '100%': { transform: 'translate(0px, 0px) scale(1)' }
                                },
                                float: {
                                    '0%, 100%': { transform: 'translateY(0)' },
                                    '50%': { transform: 'translateY(-10px)' },
                                }
                            }
                        }
                    }
                }
            </script>
            <style>
                @import url('https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@300;400;500;600;700&family=JetBrains+Mono:wght@400;500;700&display=swap');
                
                body {
                    font-family: 'Space Grotesk', sans-serif;
                    background-color: #060B12;
                    overflow-x: hidden;
                }
                .mono {
                    font-family: 'JetBrains Mono', monospace;
                }
                
                /* Glassmorphism */
                .glass-panel {
                    background: rgba(10, 18, 31, 0.4);
                    backdrop-filter: blur(16px);
                    -webkit-backdrop-filter: blur(16px);
                    border: 1px solid rgba(0, 210, 255, 0.1);
                    box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
                }
                
                /* Advanced Glow Utilities */
                .glow-text {
                    text-shadow: 0 0 20px rgba(0, 210, 255, 0.5);
                }
                .glow-border-hover {
                    transition: all 0.3s ease;
                }
                .glow-border-hover:hover {
                    border-color: rgba(0, 210, 255, 0.5);
                    box-shadow: 0 0 20px rgba(0, 210, 255, 0.15), inset 0 0 10px rgba(0, 210, 255, 0.05);
                }
                
                /* Cool Buttons */
                .cyber-btn {
                    position: relative;
                    overflow: hidden;
                    transition: all 0.3s ease;
                    z-index: 1;
                }
                .cyber-btn::before {
                    content: '';
                    position: absolute;
                    top: 0; left: 0; right: 0; bottom: 0;
                    background: linear-gradient(45deg, rgba(0, 210, 255, 0.1), rgba(16, 185, 129, 0.1));
                    z-index: -1;
                    transition: opacity 0.3s ease;
                    opacity: 0;
                }
                .cyber-btn:hover::before {
                    opacity: 1;
                }
                .cyber-btn:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 8px 25px -5px rgba(0, 210, 255, 0.3);
                }
                .cyber-btn:active {
                    transform: translateY(1px);
                }

                /* Scrollbar */
                ::-webkit-scrollbar { width: 6px; }
                ::-webkit-scrollbar-track { background: rgba(10, 18, 31, 0.5); }
                ::-webkit-scrollbar-thumb { 
                    background: rgba(0, 210, 255, 0.2); 
                    border-radius: 4px;
                }
                ::-webkit-scrollbar-thumb:hover { background: rgba(0, 210, 255, 0.5); }

                /* Toast */
                .toast-enter { animation: slideInRight 0.4s cubic-bezier(0.4, 0, 0.2, 1) forwards; }
                .toast-exit { animation: slideOutRight 0.3s ease-in forwards; }
                @keyframes slideInRight {
                    from { transform: translateX(100%); opacity: 0; }
                    to { transform: translateX(0); opacity: 1; }
                }
                @keyframes slideOutRight {
                    from { transform: translateX(0); opacity: 1; }
                    to { transform: translateX(100%); opacity: 0; }
                }
            </style>
        </head>
        <body class="text-slate-300 min-h-screen relative flex flex-col selection:bg-cyber-glow selection:text-black">
            
            <!-- Animated Background Blobs -->
            <div class="fixed inset-0 overflow-hidden pointer-events-none z-[-1]">
                <div class="absolute top-[-10%] left-[-10%] w-96 h-96 bg-cyan-600/20 rounded-full mix-blend-screen filter blur-[100px] opacity-50 animate-blob"></div>
                <div class="absolute top-[20%] right-[-5%] w-72 h-72 bg-emerald-600/10 rounded-full mix-blend-screen filter blur-[80px] opacity-50 animate-blob" style="animation-delay: 2s;"></div>
                <div class="absolute bottom-[-10%] left-[20%] w-80 h-80 bg-blue-600/20 rounded-full mix-blend-screen filter blur-[100px] opacity-40 animate-blob" style="animation-delay: 4s;"></div>
            </div>

            <!-- Toast Container -->
            <div id="toast-container" class="fixed bottom-6 right-6 z-50 flex flex-col gap-3"></div>

            <div class="max-w-6xl w-full mx-auto px-4 py-6 md:py-10 flex-1 flex flex-col">
                
                <!-- Navbar / Header -->
                <header class="glass-panel rounded-2xl p-4 md:p-6 mb-8 flex flex-col sm:flex-row items-center justify-between gap-4 relative overflow-hidden group">
                    <div class="absolute inset-0 bg-gradient-to-r from-transparent via-cyan-500/5 to-transparent -translate-x-full group-hover:animate-[shimmer_2s_infinite]"></div>
                    <style>@keyframes shimmer { 100% { transform: translateX(100%); } }</style>
                    
                    <div class="flex items-center gap-4 relative z-10 w-full justify-between sm:w-auto sm:justify-start">
                        <div class="flex items-center gap-4">
                            <div class="relative flex items-center justify-center w-12 h-12 md:w-14 md:h-14 rounded-xl bg-cyber-800 border border-cyan-500/30 overflow-hidden group-hover:border-cyan-400/50 transition-colors duration-500 shrink-0">
                                <div class="absolute inset-0 bg-cyan-400/10 animate-pulse-slow"></div>
                                <svg class="w-6 h-6 md:w-7 md:h-7 text-cyan-400 relative z-10 animate-float" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
                                </svg>
                            </div>
                            <div>
                                <h1 class="text-xl md:text-2xl font-bold text-white tracking-tight flex items-center gap-2">
                                    AXXO VAULT
                                </h1>
                                <p class="text-xs md:text-sm text-cyan-200/60 font-medium">Encrypted Local Subnet</p>
                            </div>
                        </div>
                    </div>
                    
                    <div class="flex items-center self-start sm:self-auto gap-3 bg-cyber-800/80 rounded-xl px-4 py-2 border border-slate-700/50 shadow-[inset_0_2px_4px_rgba(0,0,0,0.4)] relative z-10">
                        <div class="relative flex items-center justify-center w-2.5 h-2.5">
                            <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                            <span class="relative inline-flex rounded-full h-2 w-2 bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.8)]"></span>
                        </div>
                        <span class="text-[10px] md:text-xs font-semibold uppercase tracking-widest text-emerald-400 mono">Link Active: ${"$"}{ip}</span>
                    </div>
                </header>

                <!-- Main Content Grid -->
                <main class="grid grid-cols-1 lg:grid-cols-12 gap-6 md:gap-8 flex-1">
                    
                    <!-- Left Column: Transmit -->
                    <section class="lg:col-span-5 h-full">
                        <div class="glass-panel rounded-3xl p-5 md:p-8 h-full flex flex-col glow-border-hover relative">
                            <!-- Background accent line -->
                            <div class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-1/2 bg-gradient-to-b from-transparent via-cyan-500/50 to-transparent rounded-r-full"></div>

                            <div class="mb-6 flex items-center gap-3">
                                <div class="w-10 h-10 rounded-full bg-cyan-950/50 border border-cyan-500/20 flex items-center justify-center shrink-0">
                                    <svg class="w-5 h-5 text-cyan-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"></path>
                                    </svg>
                                </div>
                                <div>
                                    <h2 class="text-lg md:text-xl font-bold text-white glow-text">Transmit Signal</h2>
                                    <p class="text-[11px] md:text-xs text-slate-400 mt-1">Beam data securely to the local host server.</p>
                                </div>
                            </div>
                            
                            <!-- Dropzone -->
                            <div id="drop-zone" class="flex-1 flex flex-col items-center justify-center border-2 border-dashed border-cyan-900/50 bg-cyber-800/40 hover:bg-cyber-800/80 rounded-2xl p-6 md:p-8 cursor-pointer transition-all duration-300 group min-h-[200px] md:min-h-[250px]">
                                <div class="relative w-16 h-16 md:w-20 md:h-20 mb-4 md:mb-6 flex items-center justify-center">
                                    <!-- Rotating dashed circle -->
                                    <div class="absolute inset-0 rounded-full border border-dashed border-cyan-500/30 group-hover:rotate-180 transition-transform duration-1000 ease-in-out"></div>
                                    <div class="w-12 h-12 md:w-14 md:h-14 rounded-full bg-cyan-950/60 flex items-center justify-center border border-cyan-500/40 group-hover:shadow-[0_0_20px_rgba(0,210,255,0.4)] transition-all">
                                        <svg class="w-5 h-5 md:w-6 md:h-6 text-cyan-400 group-hover:-translate-y-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"></path>
                                        </svg>
                                    </div>
                                </div>
                                <h3 class="text-sm md:text-base font-semibold text-white mb-2 text-center">Click or Drag Files Here</h3>
                                <p class="text-[10px] md:text-xs text-slate-400 text-center max-w-[200px]">Supports all formats. Directly encrypted over LAN.</p>
                                <input type="file" id="file-input" class="hidden" multiple />
                            </div>
                            
                            <!-- Active Upload Panel -->
                            <div id="upload-panel" class="hidden mt-6 bg-cyber-800 rounded-2xl border border-cyan-500/20 p-4 overflow-hidden relative">
                                <div class="absolute top-0 left-0 right-0 h-[1px] bg-gradient-to-r from-transparent via-cyan-400 to-transparent"></div>
                                
                                <div class="flex items-center justify-between mb-3 text-sm">
                                    <div class="flex items-center gap-2 overflow-hidden shrink min-w-0 pr-2">
                                        <svg class="w-3.5 h-3.5 md:w-4 md:h-4 text-cyan-400 shrink-0 animate-spin" viewBox="0 0 24 24" fill="none">
                                          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" stroke-opacity="0.25"></circle>
                                          <path fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                        </svg>
                                        <span id="upload-filename" class="font-medium text-white truncate max-w-[120px] sm:max-w-[180px] text-xs md:text-sm">processing.bin</span>
                                    </div>
                                    <span id="upload-percent" class="text-xs md:text-sm font-bold text-cyan-400 mono shrink-0">0%</span>
                                </div>
                                
                                <div class="w-full bg-slate-900 h-2 rounded-full overflow-hidden shadow-inner">
                                    <div id="upload-progress-bar" class="bg-gradient-to-r from-cyan-600 to-cyan-300 h-full rounded-full transition-all duration-150 ease-out shadow-[0_0_10px_rgba(0,210,255,0.8)]" style="width: 0%"></div>
                                </div>
                                
                                <div class="flex justify-between items-center text-[10px] md:text-[11px] text-cyan-200/50 mt-2 font-mono">
                                    <span id="upload-speed">0 KB/s</span>
                                    <span id="upload-bytes">0.0 / 0.0 MB</span>
                                </div>
                            </div>
                        </div>
                    </section>

                    <!-- Right Column: Shared Directory -->
                    <section class="lg:col-span-7 h-full">
                        <div class="glass-panel rounded-3xl p-5 md:p-8 flex flex-col h-full lg:min-h-[500px]">
                            
                            <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
                                <div class="flex items-center gap-3 shrink-0">
                                    <div class="w-10 h-10 rounded-full bg-indigo-950/50 border border-indigo-500/20 flex items-center justify-center shrink-0">
                                        <svg class="w-5 h-5 text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
                                        </svg>
                                    </div>
                                    <div>
                                        <h2 class="text-lg md:text-xl font-bold text-white glow-text">Server Storage</h2>
                                        <p class="text-[11px] md:text-xs text-slate-400 mt-1">Available data payloads on vault.</p>
                                    </div>
                                </div>
                                
                                <div class="flex gap-2 w-full sm:w-auto mt-2 sm:mt-0">
                                    <div class="relative flex-1 sm:w-48">
                                        <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                            <svg class="w-3.5 h-3.5 md:w-4 md:h-4 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path></svg>
                                        </div>
                                        <input type="text" id="search-input" placeholder="Query files..." class="w-full bg-cyber-800 border border-slate-700/50 text-white text-xs rounded-xl pl-8 md:pl-9 pr-3 py-2 md:py-2.5 focus:outline-none focus:border-cyan-400 focus:shadow-[0_0_10px_rgba(0,210,255,0.2)] transition-all" oninput="filterFiles()">
                                    </div>
                                    <button onclick="fetchFiles()" class="cyber-btn bg-cyber-800 border border-slate-700/50 hover:border-cyan-500/40 text-slate-300 hover:text-cyan-400 rounded-xl px-3 py-2 md:py-2.5 flex items-center justify-center shrink-0" title="Refresh Directory">
                                        <svg class="w-3.5 h-3.5 md:w-4 md:h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 1121.21 15.172M4 4v5h.582m15.356 2A8.001 8.001 0 1121.21 15.172"></path></svg>
                                    </button>
                                </div>
                            </div>

                            <!-- Interactive List Area -->
                            <div class="bg-cyber-900/50 rounded-2xl border border-slate-800/80 overflow-hidden flex-1 flex flex-col relative min-h-[250px] md:min-h-[300px]">
                                <div class="absolute inset-0 z-0 opacity-10 pointer-events-none" style="background-image: linear-gradient(rgba(0, 210, 255, 0.4) 1px, transparent 1px), linear-gradient(90deg, rgba(0, 210, 255, 0.4) 1px, transparent 1px); background-size: 20px 20px;"></div>
                                
                                <div id="files-list" class="space-y-2 overflow-y-auto flex-1 p-2 md:p-3 relative z-10 w-full min-w-0">
                                    <div class="h-full flex flex-col items-center justify-center text-slate-500 space-y-4 pt-10 pb-4">
                                        <svg class="w-8 h-8 animate-spin text-cyan-400/50" viewBox="0 0 24 24" fill="none">
                                            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" stroke-opacity="0.1"></circle>
                                            <path fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
                                        </svg>
                                        <span class="text-[10px] md:text-xs font-mono tracking-widest uppercase">Connecting to Peer...</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>
                </main>
                
                <footer class="mt-6 md:mt-8 flex flex-col sm:flex-row items-center justify-between gap-3 text-[10px] md:text-xs text-slate-500 mono py-4 md:py-6 border-t border-slate-800/60">
                    <div class="text-center sm:text-left">AXXO VAULT OS &middot; v1.0.0-STABLE</div>
                    <div class="flex items-center gap-2">
                        <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 shadow-[0_0_5px_#10B981]"></span>
                        SYSTEM ONLINE | LATENCY < 1ms
                    </div>
                </footer>
            </div>

            <script>
                // --- Core Logic ---
                const fileInput = document.getElementById('file-input');
                const dropZone = document.getElementById('drop-zone');
                const searchInput = document.getElementById('search-input');
                let allFiles = [];

                dropZone.addEventListener('click', () => fileInput.click());
                
                dropZone.addEventListener('dragover', (e) => {
                    e.preventDefault();
                    dropZone.classList.add('border-cyan-400', 'bg-cyan-950/20');
                    dropZone.classList.remove('border-cyan-900/50', 'bg-cyber-800/40');
                });
                ['dragleave', 'drop'].forEach(evt => {
                    dropZone.addEventListener(evt, (e) => {
                        e.preventDefault();
                        dropZone.classList.remove('border-cyan-400', 'bg-cyan-950/20');
                        dropZone.classList.add('border-cyan-900/50', 'bg-cyber-800/40');
                        if (evt === 'drop' && e.dataTransfer.files.length > 0) {
                            handleUpload(e.dataTransfer.files);
                        }
                    });
                });

                fileInput.addEventListener('change', () => {
                    if (fileInput.files.length > 0) {
                        handleUpload(fileInput.files);
                    }
                });

                function handleUpload(files) {
                    uploadSingleFile(files[0]);
                }

                function uploadSingleFile(file) {
                    const panel = document.getElementById('upload-panel');
                    const filenameEl = document.getElementById('upload-filename');
                    const percentEl = document.getElementById('upload-percent');
                    const barEl = document.getElementById('upload-progress-bar');
                    const speedEl = document.getElementById('upload-speed');
                    const bytesEl = document.getElementById('upload-bytes');

                    panel.classList.remove('hidden');
                    filenameEl.textContent = file.name;
                    percentEl.textContent = '0%';
                    barEl.style.width = '0%';
                    speedEl.textContent = 'Preparing...';
                    bytesEl.textContent = '';
                    
                    const formData = new FormData();
                    formData.append("file", file);

                    const xhr = new XMLHttpRequest();
                    let startTime = Date.now();
                    let lastLoaded = 0;
                    let lastTime = startTime;

                    xhr.upload.addEventListener('progress', (e) => {
                        if (e.lengthComputable) {
                            const percent = Math.round((e.loaded / e.total) * 100);
                            percentEl.textContent = percent + '%';
                            barEl.style.width = percent + '%';
                            
                            const now = Date.now();
                            const elapsedSec = (now - lastTime) / 1000;
                            if (elapsedSec > 0.5) {
                                const bytesDiff = e.loaded - lastLoaded;
                                const speedBytes = bytesDiff / elapsedSec;
                                let speedStr = speedBytes > (1024 * 1024) 
                                    ? (speedBytes / (1024 * 1024)).toFixed(1) + " MB/s" 
                                    : (speedBytes / 1024).toFixed(1) + " KB/s";
                                speedEl.textContent = speedStr;
                                lastLoaded = e.loaded;
                                lastTime = now;
                            }
                            
                            const loadedMB = (e.loaded / (1024 * 1024)).toFixed(1);
                            const totalMB = (e.total / (1024 * 1024)).toFixed(1);
                            bytesEl.textContent = loadedMB + ' / ' + totalMB + ' MB';
                        }
                    });

                    xhr.onreadystatechange = () => {
                        if (xhr.readyState === XMLHttpRequest.DONE) {
                            if (xhr.status === 200) {
                                barEl.classList.add('bg-emerald-500'); 
                                setTimeout(() => {
                                    panel.classList.add('hidden');
                                    barEl.classList.remove('bg-emerald-500');
                                    showToast('Signal Beamed', `'${"$"}{file.name}' received by vault.`, 'success');
                                    fetchFiles();
                                }, 1500);
                            } else {
                                panel.classList.add('hidden');
                                showToast('Transmission Failed', xhr.responseText || 'Connection dropped.', 'error');
                            }
                        }
                    };

                    xhr.open('POST', '/upload', true);
                    xhr.send(formData);
                }

                function showToast(title, msg, type = 'success') {
                    const cont = document.getElementById('toast-container');
                    const toast = document.createElement('div');
                    toast.className = `toast-enter glass-panel border ${'$'}{type==='success' ? 'border-emerald-500/50' : 'border-rose-500/50'} rounded-xl p-3 md:p-4 flex items-start gap-3 shadow-2xl max-w-[90vw] md:max-w-sm`;
                    
                    const icon = type === 'success' 
                        ? `<svg class="w-4 h-4 md:w-5 md:h-5 text-emerald-400 mt-0.5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>`
                        : `<svg class="w-4 h-4 md:w-5 md:h-5 text-rose-400 mt-0.5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>`;

                    toast.innerHTML = `
                        ${'$'}{icon}
                        <div class="min-w-0">
                            <h4 class="text-xs md:text-sm font-semibold text-white truncate">${'$'}{title}</h4>
                            <p class="text-[10px] md:text-xs text-slate-300 mt-0.5 md:mt-1 break-words line-clamp-2">${'$'}{msg}</p>
                        </div>
                    `;
                    cont.appendChild(toast);
                    
                    setTimeout(() => {
                        toast.classList.replace('toast-enter', 'toast-exit');
                        setTimeout(() => toast.remove(), 400);
                    }, 4000);
                }

                function formatSize(bytes) {
                    if (bytes === 0) return '0 B';
                    const k = 1024;
                    const sizes = ['B', 'KB', 'MB', 'GB'];
                    const i = Math.floor(Math.log(bytes) / Math.log(k));
                    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
                }

                function getCyberIcon(filename) {
                    const ext = filename.split('.').pop().toLowerCase();
                    const imgs = ['png', 'jpg', 'jpeg', 'gif', 'svg', 'webp'];
                    const docs = ['pdf', 'doc', 'docx', 'txt', 'csv', 'md'];
                    const media = ['mp4', 'mp3', 'wav', 'mkv', 'avi'];
                    const code = ['js', 'kt', 'html', 'css', 'py', 'json', 'xml'];
                    const archives = ['zip', 'rar', 'tar', 'gz'];
                    
                    let colorClass = "text-indigo-400";
                    let bgClass = "bg-indigo-950/40";
                    let iconPath = `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z"></path>`;

                    if (imgs.includes(ext)) {
                        colorClass = "text-emerald-400"; bgClass = "bg-emerald-950/40";
                        iconPath = `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path>`;
                    } else if (docs.includes(ext)) {
                        colorClass = "text-amber-400"; bgClass = "bg-amber-950/40";
                        iconPath = `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>`;
                    } else if (media.includes(ext)) {
                        colorClass = "text-rose-400"; bgClass = "bg-rose-950/40";
                        iconPath = `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>`;
                    } else if (archives.includes(ext)) {
                        colorClass = "text-fuchsia-400"; bgClass = "bg-fuchsia-950/40";
                        iconPath = `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4"></path>`;
                    } else if (code.includes(ext)) {
                        colorClass = "text-cyan-400"; bgClass = "bg-cyan-950/40";
                        iconPath = `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M10 20l4-16m4 4l4 4-4 4M6 16l-4-4 4-4"></path>`;
                    }

                    return `
                        <div class="w-10 h-10 md:w-12 md:h-12 rounded-xl ${"$"}{bgClass} flex items-center justify-center border border-white/5 shrink-0 relative overflow-hidden group-hover:scale-105 transition-transform">
                            <svg class="w-5 h-5 md:w-6 md:h-6 ${"$"}{colorClass} relative z-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">${"$"}{iconPath}</svg>
                            <div class="absolute inset-0 bg-gradient-to-tr from-transparent via-white/5 to-transparent"></div>
                        </div>
                    `;
                }

                function fetchFiles() {
                    const listEl = document.getElementById('files-list');
                    
                    fetch('/api/files')
                        .then(res => res.json())
                        .then(data => {
                            allFiles = data;
                            renderFiles(data);
                        })
                        .catch(err => {
                            console.error(err);
                            listEl.innerHTML = `
                                <div class="h-full flex flex-col items-center justify-center pt-10 pb-6 space-y-3 px-4 text-center">
                                    <svg class="w-8 h-8 text-rose-500/50" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                                    <div class="text-rose-400 text-xs font-semibold uppercase tracking-wider">Link Severed</div>
                                    <div class="text-[10px] text-slate-500 font-mono text-center">Retrying handshake...</div>
                                </div>
                            `;
                        });
                }

                function renderFiles(files) {
                    const listEl = document.getElementById('files-list');
                    if (files.length === 0) {
                        listEl.innerHTML = `
                            <div class="h-full flex flex-col items-center justify-center pt-8 md:pt-16 pb-8 space-y-4 px-4">
                                <div class="w-12 h-12 md:w-16 md:h-16 rounded-full bg-cyber-800 border border-slate-700 flex items-center justify-center">
                                    <svg class="w-6 h-6 md:w-8 md:h-8 text-slate-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"></path></svg>
                                </div>
                                <div class="text-slate-400 text-sm font-medium">Data Vault is Empty</div>
                                <div class="text-[10px] md:text-xs text-slate-500 font-mono text-center max-w-[200px] md:max-w-[250px]">To populate, deploy payloads from device or transmit via this interface.</div>
                            </div>
                        `;
                        return;
                    }

                    listEl.innerHTML = files.map((file, i) => `
                        <div class="group relative overflow-hidden bg-cyber-800/80 hover:bg-cyber-800 border border-slate-700/50 hover:border-cyan-500/30 rounded-xl p-3 flex align-middle flex-row items-center justify-between gap-3 transition-all duration-300 transform origin-left w-full" style="animation: slideInRight 0.3s ease-out ${"$"}{i * 0.05}s forwards; opacity: 0;">
                            <div class="absolute inset-0 bg-gradient-to-r from-cyan-500/0 via-cyan-500/5 to-cyan-500/0 opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none"></div>
                            
                            <div class="flex items-center gap-3 min-w-0 z-10 flex-1 w-0"> <!-- w-0 ensures truncate works -->
                                ${"$"}{getCyberIcon(file.name)}
                                <div class="min-w-0 flex flex-col justify-center">
                                    <div class="text-xs md:text-sm font-semibold text-white truncate hover:text-cyan-300 transition-colors w-full" title="${"$"}{file.name}">
                                        ${"$"}{file.name}
                                    </div>
                                    <div class="flex items-center gap-2 mt-1">
                                        <span class="text-[10px] text-cyan-500/70 font-mono px-1.5 py-0.5 rounded bg-cyan-950/40 border border-cyan-900/50">
                                            ${"$"}{formatSize(file.size)}
                                        </span>
                                    </div>
                                </div>
                            </div>
                            
                            <a href="/download/${"$"}{file.id}" download="${"$"}{file.name}" class="cyber-btn shrink-0 text-[10px] uppercase font-bold tracking-widest text-cyan-300 bg-cyan-950/30 border border-cyan-500/30 px-3 py-2 md:px-5 md:py-2.5 rounded-lg flex items-center justify-center gap-1.5 z-10 text-center relative pointer-events-auto">
                                <svg class="w-3.5 h-3.5 group-hover:translate-y-px transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"></path>
                                </svg>
                                <span class="hidden sm:inline">Extract</span>
                            </a>
                        </div>
                    `).join('');
                }

                function filterFiles() {
                    const filterText = searchInput.value.toLowerCase();
                    const filtered = allFiles.filter(f => f.name.toLowerCase().includes(filterText));
                    renderFiles(filtered);
                }

                // Initial fetch & auto poll
                fetchFiles();
                setInterval(fetchFiles, 3000);
            </script>
        </body>
        </html>
        """.trimIndent()
    }
}
