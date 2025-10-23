const API_URL = "http://10.130.46.131:8080";

// Função uploadToAPI fora do escopo para acesso global
async function uploadToAPI(file) {
  console.log("Iniciando upload - Arquivo:", file.name, "Tipo:", file.type, "Tamanho:", file.size);

  const username = localStorage.getItem("username");
  if (!username) {
    alert("Por favor, faça login primeiro para realizar o upload.");
    return { error: "Login required" };
  }

  const formData = new FormData();
  formData.append("username", username);
  formData.append("file", file);
  console.log("FormData entries:", Array.from(formData.entries()));

  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), 60000);
  const progressContainer = document.querySelector(".progress-container");

  try {
    const response = await fetch(`${API_URL}/api/audio/upload`, {
      method: "POST",
      body: formData,
      signal: controller.signal,
      headers: {
        "Access-Control-Allow-Origin": "*",
        "Accept": "application/json",
      },
    });
    clearTimeout(timeoutId);
    console.log("Resposta do servidor:", response.status, response.statusText);
    if (!response.ok) throw new Error(`Erro HTTP: ${response.status}`);
    const data = await response.json();
    console.log("Dados recebidos:", data);
    console.log("Texto transcrito:", data.text);
    if (data.text) {
      document.querySelector('.convert input[type="text"]').value = data.text;
    }
    alert("Upload concluído com sucesso!");
    return data;
  } catch (error) {
    clearTimeout(timeoutId);
    console.error("Erro detalhado:", error.name, error.message);
    alert("Erro ao conectar com o servidor: " + error.message);
    return { error: error.message };
  }finally {
    if (progressContainer) progressContainer.style.display = "none";
  }
}

document.addEventListener("DOMContentLoaded", function () {
  // 🔹 Navbar funcionando em todas as páginas
  const loginButton = document.getElementById("loginButton");
  if (loginButton) loginButton.addEventListener("click", () => window.location.href = "login.html");

  const inicioButton = document.getElementById("inicioButton");
  if (inicioButton) inicioButton.addEventListener("click", () => window.location.href = "home.html");

  const historicoButton = document.getElementById("historicoButton");
  if (historicoButton) historicoButton.addEventListener("click", () => window.location.href = "historico_transacoes.html");

  const cadastroButton = document.getElementById("cadastroButton");
  if (cadastroButton) cadastroButton.addEventListener("click", () => window.location.href = "cadastro.html");

  const loginButton2 = document.getElementById("loginButton2");
  if (loginButton2) loginButton2.addEventListener("click", () => window.location.href = "login.html");

  // 🔹 Código de upload — executa apenas se existir dropZone
  const dropZone = document.getElementById("dropZone");
  if (dropZone) {
    const fileInput = document.getElementById("fileInput");
    const browseBtn = document.querySelector(".browse-btn");

    ["dragenter", "dragover", "dragleave", "drop"].forEach((eventName) =>
      dropZone.addEventListener(eventName, preventDefaults, false)
    );

    function preventDefaults(e) {
      e.preventDefault();
      e.stopPropagation();
    }

    ["dragenter", "dragover"].forEach((eventName) =>
      dropZone.addEventListener(eventName, highlight, false)
    );

    ["dragleave", "drop"].forEach((eventName) =>
      dropZone.addEventListener(eventName, unhighlight, false)
    );

    function highlight() {
      dropZone.classList.add("highlight");
    }

    function unhighlight() {
      dropZone.classList.remove("highlight");
    }

    dropZone.addEventListener("drop", handleDrop, false);

    function handleDrop(e) {
      const files = e.dataTransfer.files;
      handleFiles(files);
    }

    browseBtn.addEventListener("click", () => fileInput.click());

    fileInput.addEventListener("change", function () {
      handleFiles(this.files);
    });

    async function handleFiles(files) {
      if (files.length) {
        const file = files[0];
        console.log("Arquivo detectado:", file.name, "Tipo:", file.type);
        if (!file.type.match("audio.*")) {
          alert("Selecione um arquivo de áudio (MP3, WAV, OGG).");
          return;
        }
        try {
          await uploadToAPI(file);
        } catch (error) {
          console.error("Erro no processamento:", error);
          alert("Erro ao processar: " + error.message);
        }
        dropZone.innerHTML = `
          <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#4CAF50" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
            <polyline points="22 4 12 14.01 9 11.01"></polyline>
          </svg>
          <h1>Arquivo pronto</h1>
          <p>${file.name}</p>
          <button class="browse-btn">Selecionar outro</button>
        `;
        document.querySelector(".browse-btn").addEventListener("click", () => fileInput.click());
      }
    }
  }

  // 🔹 Login
  const apiURL = `${API_URL}/api/accounts/login`;
  const loginForm = document.getElementById("loginForm");
  if (loginForm) {
    loginForm.addEventListener("submit", async function (e) {
      e.preventDefault();
      const user = document.getElementById("user").value.trim();
      const password = document.getElementById("password").value;

      try {
        const response = await fetch(apiURL, {
          method: "POST",
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
          body: new URLSearchParams({ username: user, password: password })
        });

        if (!response.ok) throw new Error(`Erro HTTP: ${response.status}`);

        const data = await response.text();
        console.log("Dados do login:", data);
        localStorage.setItem("username", user);
        alert("Login bem-sucedido!");
        window.location.href = "home.html";
      } catch (error) {
        console.error("Erro no login:", error);
        alert("Usuário ou senha incorretos! " + error.message);
      }
    });
  }

  // 🔹 Cadastro
  const cadastroApiURL = `${API_URL}/api/accounts/cadastro`;
  const cadastroForm = document.getElementById("cadastroForm");
  if (cadastroForm) {
    cadastroForm.addEventListener("submit", async function (e) {
      e.preventDefault();
      const user = document.getElementById("user").value.trim();
      const password = document.getElementById("password").value;

      if (!user || !password) {
        alert("Preencha todos os campos!");
        return;
      }

      try {
        console.log("Iniciando cadastro para usuário:", user);
        const response = await fetch(cadastroApiURL, {
          method: "POST",
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
          body: new URLSearchParams({ username: user, password: password })
        });

        if (!response.ok) throw new Error(`Erro HTTP: ${response.status}`);

        const data = await response.text();
        console.log("Dados do cadastro:", data);
        alert("Cadastro bem-sucedido!");
        window.location.href = "login.html";
      } catch (error) {
        console.error("Erro no cadastro:", error);
        alert("Erro ao cadastrar: " + error.message);
      }
    });
  }

    // Função para formatar a data no formato DD/MM/YYYY
  function formatDate(isoDate) {
    if (!isoDate) return "Data não disponível";
    const date = new Date(isoDate);
    return date.toLocaleDateString("pt-BR"); // Formato DD/MM/YYYY
  }

  // Novo método para buscar o histórico de transcrições
  async function fetchHistorico() {
    const username = localStorage.getItem("username");
    if (!username) {
      alert("Por favor, faça login para visualizar o histórico.");
      window.location.href = "login.html";
      return;
    }

    const historicoApiURL = `${API_URL}/api/accounts/historico?username=${encodeURIComponent(username)}`;
    const historicoContainer = document.querySelector(".aparecer");

    if (historicoContainer) {
      // Exibe o indicador de carregamento
      historicoContainer.innerHTML = '<p class="loading">Carregando histórico...</p>';

      try {
        const response = await fetch(historicoApiURL, {
          method: "GET",
          headers: {
            "Accept": "application/json",
          },
        });

        if (!response.ok) {
          throw new Error(`Erro HTTP: ${response.status}`);
        }

        const data = await response.json();
        console.log("Histórico recebido:", data);

        // Verifica se a resposta é um array
        if (Array.isArray(data)) {
          // Limpa o indicador de carregamento
          historicoContainer.innerHTML = "";

          // Verifica se há registros no histórico
          if (data.length === 0) {
            historicoContainer.innerHTML = '<p class="error">Nenhum histórico encontrado.</p>';
            return;
          }

          // Itera sobre o array de históricos e adiciona ao DOM
          data.forEach(item => {
            console.log("Adicionando item ao histórico:", item);
            const div = document.createElement("div");
            div.className = "desenrola-bati-joga-de-ladinho";
            div.innerHTML = `
              <p>${item.nomeArquivo || "Nome não disponível"}</p>
              <p>${item.dataTranscricao}</p>
              <div class="baixar">
                <p class="baixar-btn" data-id="${item.id}">Baixar</p>
              </div>
            `;
            historicoContainer.appendChild(div);
          });

          document.querySelectorAll(".baixar-btn").forEach(button => {
          button.addEventListener("click", async function () {
            const id = this.getAttribute("data-id");
            const filename = `arquivo_${id}.txt`; 

            try {
              const response = await fetch(`${API_URL}/api/accounts/getArchive?username=${encodeURIComponent(username)}&id=${encodeURIComponent(id)}`, {
                method: "GET",
                headers: {
                  "Accept": "text/plain"
                },
              });

              if (!response.ok) throw new Error(`Erro HTTP: ${response.status}`);

              const text = await response.text();
              const blob = new Blob([text], { type: "text/plain" });
              const url = window.URL.createObjectURL(blob);

              const a = document.createElement("a");
              a.href = url;
              a.download = filename;
              document.body.appendChild(a);
              a.click();

              a.remove();
              window.URL.revokeObjectURL(url);
            } catch (error) {
              console.error("Erro ao baixar arquivo:", error);
              alert("Erro ao baixar o arquivo: " + error.message);
            }
          });
        });

        } else {
          throw new Error("Resposta do servidor não é um array.");
        }
      } catch (error) {
        console.error("Erro ao buscar histórico:", error);
        historicoContainer.innerHTML = '<p class="error">Erro ao carregar o histórico. Tente novamente mais tarde.</p>';
      }
    }
  }

  // Executa a função fetchHistorico se a página for historico_transacoes.html
  if (window.location.pathname.includes("historico_transacoes.html")) {
    fetchHistorico();
  }

  document.getElementById("loginButton2")?.addEventListener("click", () => window.location.href = "login.html");
  document.getElementById("cadastroButton")?.addEventListener("click", () => window.location.href = "cadastro.html");
});
