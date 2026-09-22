package model;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import view.menu_GUI;
import view.splah_GUI;

/**
 * @author alunos
 */
public class funcoes_DAO {

    private menu_GUI view;

    // Construtor que recebe a referência da View (Injeção de Dependência)
    public funcoes_DAO(menu_GUI view) {
        this.view = view;
    }

    /**
     * Adiciona um produto ao carrinho
     */
    public void adicionarItem(String item, double preco, int kcal) {
        DefaultTableModel model = (DefaultTableModel) view.getTabelaAtivos().getModel();
        model.addRow(new Object[]{item, String.format("R$ %.2f", preco), kcal + " kcal"});
        atualizarTotais();
    }

    /**
     * Recalcula o valor total e o total de calorias
     */
    public void atualizarTotais() {
        DefaultTableModel model = (DefaultTableModel) view.getTabelaAtivos().getModel();
        double totalPreco = 0.0;
        int totalKcal = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            // Trata o preço (remove "R$ " e troca vírgula por ponto)
            String precoStr = model.getValueAt(i, 1).toString()
                    .replace("R$", "")
                    .replace(" ", "")
                    .replace(",", ".");
            totalPreco += Double.parseDouble(precoStr);

            // Trata as calorias (remove " kcal")
            String kcalStr = model.getValueAt(i, 2).toString()
                    .replace("kcal", "")
                    .replace(" ", "");
            totalKcal += Integer.parseInt(kcalStr);
        }

        // Atualiza as labels na View através dos Getters
        view.getValtotTxt().setText(String.format("R$ %.2f", totalPreco));
        view.getTotalkcalTxt().setText(totalKcal + " kcal");
    }

    /**
     * Remove o item selecionado na tabela
     */
    public void removerItemSelecionado() {
        int linhaSelecionada = view.getTabelaAtivos().getSelectedRow();

        if (linhaSelecionada >= 0) {
            DefaultTableModel model = (DefaultTableModel) view.getTabelaAtivos().getModel();
            model.removeRow(linhaSelecionada);
            atualizarTotais();
        } else {
            JOptionPane.showMessageDialog(view, 
                    "Selecione um item na tabela para remover!", 
                    "Aviso", 
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Finaliza o pedido e limpa a tabela
     */
   public void finalizarPedido() {
    DefaultTableModel model = (DefaultTableModel) view.getTabelaAtivos().getModel();

    // 1. Valida se há itens no carrinho
    if (model.getRowCount() == 0) {
        JOptionPane.showMessageDialog(view, 
                "O seu carrinho está vazio! Adicione itens antes de finalizar.", 
                "Carrinho Vazio", 
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 2. Identificação do Cliente
    String nomeCliente = JOptionPane.showInputDialog(view, 
            "Por favor, digite o seu nome para identificação do pedido:", 
            "Identificação do Cliente", 
            JOptionPane.QUESTION_MESSAGE);

    if (nomeCliente == null || nomeCliente.trim().isEmpty()) {
        JOptionPane.showMessageDialog(view, 
                "É necessário informar o nome para prosseguir com o pedido.", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 3. Escolha e Análise da Forma de Pagamento
    String[] opcoesPagamento = {"Pix", "Cartão de Crédito/Débito", "Dinheiro"};
    int escolhaPagamento = JOptionPane.showOptionDialog(
            view,
            "Selecione a forma de pagamento:",
            "Pagamento - Lanches CEAP",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opcoesPagamento,
            opcoesPagamento[0]
    );

    // Se o cliente fechar a janela sem escolher o pagamento
    if (escolhaPagamento == -1) {
        return;
    }

    String formaPagamento = opcoesPagamento[escolhaPagamento];

    // 4. Captura Data e Hora Atual
    java.time.LocalDateTime agora = java.time.LocalDateTime.now();
    java.time.format.DateTimeFormatter formatadorData = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    String dataHoraFormatada = agora.format(formatadorData);

    // 5. Montagem do Comprovante de Impressão
    StringBuilder comprovante = new StringBuilder();
    comprovante.append("=========================================\n");
    comprovante.append("             LANCHES CEAP                \n");
    comprovante.append("          TOTEM DE AUTOATENDIMENTO       \n");
    comprovante.append("=========================================\n");
    comprovante.append("Data/Hora: ").append(dataHoraFormatada).append("\n");
    comprovante.append("Cliente: ").append(nomeCliente.trim()).append("\n");
    comprovante.append("Forma de Pagamento: ").append(formaPagamento).append("\n");
    comprovante.append("-----------------------------------------\n");
    comprovante.append("ITENS DO PEDIDO:\n\n");

    for (int i = 0; i < model.getRowCount(); i++) {
        String item = model.getValueAt(i, 0).toString();
        String preco = model.getValueAt(i, 1).toString();
        String kcal = model.getValueAt(i, 2).toString();

        comprovante.append(String.format("- %-18s | R$ %-6s | %s kcal\n", item, preco, kcal));
    }

    comprovante.append("-----------------------------------------\n");
    comprovante.append("VALOR TOTAL: R$ ").append(view.getValtotTxt().getText()).append("\n");
    comprovante.append("TOTAL CALÓRICO: ").append(view.getTotalkcalTxt().getText()).append("\n");
    comprovante.append("=========================================\n");
    comprovante.append("   Obrigado pela preferência, ").append(nomeCliente.trim()).append("!\n");
    comprovante.append("            Tenha um ótimo apetite!       \n");
    comprovante.append("=========================================\n");

    // 6. Exibição do Recibo para Impressão
    javax.swing.JTextArea areaTexto = new javax.swing.JTextArea(comprovante.toString());
    areaTexto.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
    areaTexto.setEditable(false);

    JOptionPane.showMessageDialog(view, 
            new javax.swing.JScrollPane(areaTexto), 
            "Comprovante de Impressão - Lanches CEAP", 
            JOptionPane.INFORMATION_MESSAGE);

    // 7. Limpa a tabela e reseta os totais para o próximo cliente
    model.setRowCount(0);
    atualizarTotais();

    JOptionPane.showMessageDialog(view, "Pedido enviado para a cozinha com sucesso!");
}
   
   public static void carrega(splah_GUI splash) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i <= 100; i++) {
                        // Pausa de 50 milissegundos para simular o carregamento (ajuste o tempo se necessário)
                        Thread.sleep(50);
                        
                        // Atualiza o valor da JProgressBar 'barra'
                        splash.barra.setValue(i);

                        // Altera a mensagem exibida no JLabel 'texto' conforme o progresso
                        if (i < 30) {
                            splash.texto.setText("Carregando módulos do sistema...");
                        } else if (i < 70) {
                            splash.texto.setText("Conectando ao banco de dados...");
                        } else if (i < 95) {
                            splash.texto.setText("Carregando configurações...");
                        } else {
                            splash.texto.setText("Iniciando...");
                        }
                    }

                    // Fecha a tela de Splash
                    splash.dispose();

                  
                    new menu_GUI().setVisible(true);

                } catch (InterruptedException e) {
                    JOptionPane.showMessageDialog(null, "Erro ao carregar a aplicação: " + e.getMessage());
                }
            }
        }).start();
    }
}
