package estoque.api.requests;

public class ConferenciaRequest {
    private int produtoId;
    private int pontoId;
    private int funcionarioId;
    private int estoqueInicial;
    private int reposicoes;
    private int saidas;
    private int quantidadeFisica;

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }
    public int getPontoId() { return pontoId; }
    public void setPontoId(int pontoId) { this.pontoId = pontoId; }
    public int getFuncionarioId() { return funcionarioId; }
    public void setFuncionarioId(int funcionarioId) { this.funcionarioId = funcionarioId; }
    public int getEstoqueInicial() { return estoqueInicial; }
    public void setEstoqueInicial(int estoqueInicial) { this.estoqueInicial = estoqueInicial; }
    public int getReposicoes() { return reposicoes; }
    public void setReposicoes(int reposicoes) { this.reposicoes = reposicoes; }
    public int getSaidas() { return saidas; }
    public void setSaidas(int saidas) { this.saidas = saidas; }
    public int getQuantidadeFisica() { return quantidadeFisica; }
    public void setQuantidadeFisica(int quantidadeFisica) { this.quantidadeFisica = quantidadeFisica; }
}