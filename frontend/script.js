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
            tbody.innerHTML = '<tr><td colspan="10" class="empty-msg">Nenhuma conferência</td></tr>';
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
                    <td>${c.estoqueInicial}</td>
                    <td>${c.reposicoesRecebidas}</td>
                    <td>${c.saidasRealizadas}</td>
                    <td>${c.estoqueEsperado}</td>
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


async function registrarReposicao() {
    const produtoId = document.getElementById('reposicaoProdutoId').value;
    const pontoId = document.getElementById('reposicaoPontoId').value;
    const funcionarioId = document.getElementById('reposicaoFuncionarioId').value;
    const quantidade = document.getElementById('reposicaoQuantidade').value;

    if (!produtoId || !pontoId || !funcionarioId || !quantidade) {
        return mostrarAlert('Preencha todos os campos!');
    }

    try {
        const res = await fetch(`${API_URL}/reposicoes`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                produtoId: parseInt(produtoId),
                pontoId: parseInt(pontoId),
                funcionarioId: parseInt(funcionarioId),
                quantidade: parseInt(quantidade)
            })
        });
        const msg = await handleResponse(res);
        mostrarAlert(msg);

        document.getElementById('reposicaoProdutoId').value = '';
        document.getElementById('reposicaoPontoId').value = '';
        document.getElementById('reposicaoFuncionarioId').value = '';
        document.getElementById('reposicaoQuantidade').value = '';

        listarReposicoes();
    } catch (error) {
        mostrarAlert('Erro: ' + error.message);
    }
}

async function listarReposicoes() {
    try {
        const res = await fetch(`${API_URL}/reposicoes`);
        const data = await res.json();
        const reposicoes = extrairDados(data);
        const tbody = document.getElementById('listaReposicoes');

        if (!reposicoes || reposicoes.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="empty-msg">Nenhuma reposição</td></tr>';
            return;
        }

        tbody.innerHTML = reposicoes.map(r => `
            <tr>
                <td>${r.produtoNome}</td>
                <td>${r.pontoNome}</td>
                <td>${r.quantidade}</td>
                <td>${r.funcionarioNome}</td>
                <td>${new Date(r.dataHora).toLocaleString('pt-BR')}</td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Erro ao listar reposições:', error);
    }
}


async function registrarSaida() {
    const produtoId = document.getElementById('saidaProdutoId').value;
    const pontoId = document.getElementById('saidaPontoId').value;
    const funcionarioId = document.getElementById('saidaFuncionarioId').value;
    const quantidade = document.getElementById('saidaQuantidade').value;

    if (!produtoId || !pontoId || !funcionarioId || !quantidade) {
        return mostrarAlert('Preencha todos os campos!');
    }

    try {
        const res = await fetch(`${API_URL}/movimentacoes`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                produtoId: parseInt(produtoId),
                pontoId: parseInt(pontoId),
                funcionarioId: parseInt(funcionarioId),
                quantidade: parseInt(quantidade)
            })
        });
        const msg = await handleResponse(res);
        mostrarAlert(msg);

        document.getElementById('saidaProdutoId').value = '';
        document.getElementById('saidaPontoId').value = '';
        document.getElementById('saidaFuncionarioId').value = '';
        document.getElementById('saidaQuantidade').value = '';

        listarSaidas();
    } catch (error) {
        mostrarAlert('Erro: ' + error.message);
    }
}

async function listarSaidas() {
    try {
        const res = await fetch(`${API_URL}/movimentacoes`);
        const data = await res.json();
        const saidas = extrairDados(data);
        const tbody = document.getElementById('listaSaidas');

        if (!saidas || saidas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="empty-msg">Nenhuma saída</td></tr>';
            return;
        }

        tbody.innerHTML = saidas.map(m => `
            <tr>
                <td>${m.produtoNome}</td>
                <td>${m.pontoNome}</td>
                <td>${m.quantidade}</td>
                <td>${m.funcionarioNome}</td>
                <td>${new Date(m.dataHora).toLocaleString('pt-BR')}</td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Erro ao listar saídas:', error);
    }
}


document.addEventListener('DOMContentLoaded', function() {
    listarConferencias();
    listarReposicoes();
    listarSaidas();
});