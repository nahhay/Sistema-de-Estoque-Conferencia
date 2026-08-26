const API_URL = 'http://localhost:8080/api';

function mostrarAlert(mensagem) {
    const alert = document.getElementById('customAlert');
    if (!alert) {
        alert(mensagem);
        return;
    }
    const msg = document.getElementById('alertMessage');
    msg.textContent = mensagem;
    alert.classList.add('show');
}

function fecharAlert() {
    const alert = document.getElementById('customAlert');
    if (alert) alert.classList.remove('show');
}

async function handleResponse(response) {
    const text = await response.text();
    if (!text) return '';
    try {
        const data = JSON.parse(text);
        if (data.erro) return data.erro;
        if (data.mensagem) return data.mensagem;
        return data;
    } catch {
        return text;
    }
}

function extrairDados(data) {
    if (data && data.dados) return data.dados;
    return data;
}

async function realizarConferencia() {
    const produtoId = document.getElementById('conferenciaProdutoId').value;
    const pontoId = document.getElementById('conferenciaPontoId').value;
    const funcionarioId = document.getElementById('conferenciaFuncionarioId').value;
    const quantidadeFisica = document.getElementById('conferenciaQuantidadeFisica').value;

    if (!produtoId || !pontoId || !funcionarioId || !quantidadeFisica) {
        return mostrarAlert('Preencha todos os campos!');
    }

    try {
        const res = await fetch(`${API_URL}/conferencias`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                produtoId: parseInt(produtoId),
                pontoId: parseInt(pontoId),
                funcionarioId: parseInt(funcionarioId),
                quantidadeFisica: parseInt(quantidadeFisica)
            })
        });
        const msg = await handleResponse(res);
        mostrarAlert(msg);
        
        document.getElementById('conferenciaProdutoId').value = '';
        document.getElementById('conferenciaPontoId').value = '';
        document.getElementById('conferenciaFuncionarioId').value = '';
        document.getElementById('conferenciaQuantidadeFisica').value = '';
        
        listarConferencias();
    } catch (error) {
        mostrarAlert('Erro: ' + error.message);
    }
}

async function listarConferencias() {
    try {
        const res = await fetch(`${API_URL}/conferencias`);
        const data = await res.json();
        const conferencias = extrairDados(data);
        const tbody = document.getElementById('listaConferencias');

        if (!conferencias || conferencias.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="empty-msg">Nenhuma conferência</td></tr>';
            return;
        }

        tbody.innerHTML = conferencias.map(c => {
            let divClass = 'divergencia-zero';
            let divText = c.divergencia;
            if (c.divergencia < 0) { divClass = 'divergencia-negativa'; divText = c.divergencia; }
            else if (c.divergencia > 0) { divClass = 'divergencia-positiva'; divText = '+' + c.divergencia; }

            return `
                <tr>
                    <td>${c.produtoNome}</td>
                    <td>${c.pontoNome}</td>
                    <td>${c.estoqueFisico}</td>
                    <td class="${divClass}">${divText}</td>
                    <td>${c.funcionarioNome}</td>
                    <td>${new Date(c.data).toLocaleString('pt-BR')}</td>
                </tr>
            `;
        }).join('');
    } catch (error) {
        console.error('Erro ao listar conferências:', error);
    }
}

document.addEventListener('DOMContentLoaded', function() {
    listarConferencias();
});