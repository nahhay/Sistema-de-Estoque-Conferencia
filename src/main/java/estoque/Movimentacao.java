package estoque;

import java.time.LocalDateTime;

public class Movimentacao {
    private int id;
    private Produto produto;
    private Ponto ponto;
    private Funcionario funcionario;
    private int quantidade;
    private LocalDateTime dataHora;
    private TipoMovimentacao tipo;

    public Movimentacao(int id, Produto produto, Ponto ponto, Funcionario funcionario, int quantidade, LocalDateTime dataHora, TipoMovimentacao tipo) {
        this.id = id;
        this.produto = produto;
        this.ponto = ponto;
        this.funcionario = funcionario;
        this.quantidade = quantidade;
        this.dataHora = dataHora;
        this.tipo = tipo;
    }

    public int getId() { return id; }
    public Produto getProduto() { return produto; }
    public Ponto getPonto() { return ponto; }
    public Funcionario getFuncionario() { return funcionario; }
    public int getQuantidade() { return quantidade; }
    public LocalDateTime getDataHora() { return dataHora; }
    public TipoMovimentacao getTipo() { return tipo; }
}
