document.addEventListener('DOMContentLoaded', function() {
        const dropZone = document.getElementById('dropZone');
        const fileInput = document.getElementById('fileInput');
        const browseBtn = document.querySelector('.browse-btn');

        // Adicione este trecho para o redirecionamento
        const loginButton = document.getElementById('loginButton');
        if (loginButton) {
            loginButton.addEventListener('click', function() {
                window.location.href = 'login.html';
            });
        }

        const inicioButton = document.getElementById('inicioButton');
        if (inicioButton) {
            inicioButton.addEventListener('click', function() {
                window.location.href = 'home.html';
            });
        }

        const historicoButton = document.getElementById('historicoButton');
        if (historicoButton) {
            historicoButton.addEventListener('click', function() {
                window.location.href = 'historico_transacoes.html';
            });
        }

        const cadastroButton = document.getElementById('cadastroButton');
        if (cadastroButton) {
            cadastroButton.addEventListener('click', function() {
                window.location.href = 'cadastro.html';
            });
        }
        const loginButton2 = document.getElementById('loginButton2');
        if (loginButton2) {
            loginButton2.addEventListener('click', function() {
                window.location.href = 'login.html';
            });
        }
        // Prevenir comportamentos padrão para drag and drop
        ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
            dropZone.addEventListener(eventName, preventDefaults, false);
        });

        function preventDefaults(e) {
            e.preventDefault();
            e.stopPropagation();
        }

        // Efeitos visuais ao arrastar arquivo
        ['dragenter', 'dragover'].forEach(eventName => {
            dropZone.addEventListener(eventName, highlight, false);
        });

        ['dragleave', 'drop'].forEach(eventName => {
            dropZone.addEventListener(eventName, unhighlight, false);
        });

        function highlight() {
            dropZone.classList.add('highlight');
        }

        function unhighlight() {
            dropZone.classList.remove('highlight');
        }

        // Lidar com arquivos soltos
        dropZone.addEventListener('drop', handleDrop, false);

        function handleDrop(e) {
            const dt = e.dataTransfer;
            const files = dt.files;
            handleFiles(files);
        }

        // Lidar com seleção via botão
        browseBtn.addEventListener('click', () => {
            fileInput.click();
        });

        fileInput.addEventListener('change', function() {
            handleFiles(this.files);
        });

        // Função para processar os arquivos (será expandida posteriormente)
        function handleFiles(files) {
            if (files.length) {
                const file = files[0];
                
                // Verificar se é um arquivo de áudio
                if (!file.type.match('audio.*')) {
                    alert('Por favor, selecione um arquivo de áudio.');
                    return;
                }
                
                console.log('Arquivo selecionado:', file.name);
                
                // Aqui você pode adicionar a lógica para enviar para a API
                // uploadToAPI(file);
                
                // Feedback visual
                dropZone.innerHTML = `
                    <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#4CAF50" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                        <polyline points="22 4 12 14.01 9 11.01"></polyline>
                    </svg>
                    <h1>Arquivo pronto para processamento</h1>
                    <p>${file.name}</p>
                    <button class="browse-btn">Selecionar outro arquivo</button>
                `;
                
                // Re-adicionar o event listener ao novo botão
                document.querySelector('.browse-btn').addEventListener('click', () => {
                    fileInput.click();
                });
            }
        }

        // Função para enviar para a API (exemplo)
        async function uploadToAPI(file) {
            const formData = new FormData();
            formData.append('audio', file);
            
            try {
                const response = await fetch('SUA_API_AQUI', {
                    method: 'POST',
                    body: formData
                });
                
                const data = await response.json();
                console.log('Resposta da API:', data);
                
                // Aqui você pode lidar com a resposta da API (ex: mostrar a transcrição)
                
            } catch (error) {
                console.error('Erro ao enviar arquivo:', error);
                alert('Ocorreu um erro ao enviar o arquivo.');
            }
        }
    });