package com.langchain.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class SeminovosTools {

    // -------------------------------------------------------------------------
    // Estoque simulado — em produção viria de um repositório JPA/banco de dados
    // -------------------------------------------------------------------------
    private static final List<Map<String, Object>> ESTOQUE = List.of(
            Map.of("id", 1, "marca", "Toyota", "modelo", "Corolla", "ano", 2021,
                    "km", 38000, "preco", 115000.0, "cambio", "Automático", "cor", "Prata"),
            Map.of("id", 2, "marca", "Honda", "modelo", "Civic", "ano", 2020,
                    "km", 52000, "preco", 108000.0, "cambio", "Automático", "cor", "Preto"),
            Map.of("id", 3, "marca", "Volkswagen", "modelo", "T-Cross", "ano", 2022,
                    "km", 21000, "preco", 128000.0, "cambio", "Automático", "cor", "Branco"),
            Map.of("id", 4, "marca", "Hyundai", "modelo", "HB20", "ano", 2021,
                    "km", 44000, "preco", 72000.0, "cambio", "Manual", "cor", "Vermelho"),
            Map.of("id", 5, "marca", "Chevrolet", "modelo", "Onix Plus", "ano", 2022,
                    "km", 29000, "preco", 89000.0, "cambio", "Automático", "cor", "Cinza"),
            Map.of("id", 6, "marca", "Jeep", "modelo", "Compass", "ano", 2021,
                    "km", 47000, "preco", 155000.0, "cambio", "Automático", "cor", "Azul")
    );

    // -------------------------------------------------------------------------
    // TOOL 1: Consulta de Estoque
    // -------------------------------------------------------------------------
    @Tool("Para QUALQUER pergunta sobre veículos disponíveis, estoque ou opções de carros, " +
            "SEMPRE utilize esta ferramenta, passando null em todos os filtros caso o cliente " +
            "não informe nenhum filtro específico.")
    public String consultarEstoque(String marca, String modelo, Integer anoMinimo, Double precoMaximo) {

        System.out.println(">>> TOOL ACIONADA: consultarEstoque | marca=" + marca +
                " | modelo=" + modelo +
                " | anoMinimo=" + anoMinimo +
                " | precoMaximo=" + precoMaximo);

        var resultado = ESTOQUE.stream()
                .filter(v -> marca == null || v.get("marca").toString()
                        .equalsIgnoreCase(marca))
                .filter(v -> modelo == null || v.get("modelo").toString()
                        .toLowerCase().contains(modelo.toLowerCase()))
                .filter(v -> anoMinimo == null || ((Number) v.get("ano")).intValue() >= anoMinimo)
                .filter(v -> precoMaximo == null || ((Number) v.get("preco")).doubleValue() <= precoMaximo)
                .toList();

        if (resultado.isEmpty()) {
            return "Nenhum veículo encontrado com os filtros informados no momento. " +
                    "Posso verificar outras opções ou você pode ampliar os critérios de busca.";
        }

        var sb = new StringBuilder("Veículos disponíveis no estoque:\n\n");
        resultado.forEach(v -> sb.append(String.format(
                "• %s %s %d | %,.0f km | R$ %,.2f | %s | Cor: %s\n",
                v.get("marca"), v.get("modelo"),
                ((Number) v.get("ano")).intValue(),
                ((Number) v.get("km")).doubleValue(),
                ((Number) v.get("preco")).doubleValue(),
                v.get("cambio"), v.get("cor")
        )));

        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // TOOL 2: Simulação de Financiamento
    // Taxa mensal média de mercado para seminovos: ~1,49% a.m. (referência BACEN)
    // -------------------------------------------------------------------------
    @Tool("Simula o financiamento de um veículo seminovo com base no valor do veículo, " +
            "valor de entrada e número de parcelas desejado. " +
            "Retorna o valor da parcela, total pago e custo efetivo total.")
    public String simularFinanciamento(double valorVeiculo, double valorEntrada, int numeroParcelas) {

        System.out.println(">>> TOOL ACIONADA: simularFinanciamento | valorVeiculo=" + valorVeiculo +
                " | valorEntrada=" + valorEntrada +
                " | numeroParcelas=" + numeroParcelas);

        if (valorEntrada >= valorVeiculo) {
            return "O valor de entrada informado já cobre o valor total do veículo. Nenhum financiamento necessário!";
        }
        if (numeroParcelas < 12 || numeroParcelas > 60) {
            return "O número de parcelas deve ser entre 12 e 60 meses para veículos seminovos.";
        }

        double valorFinanciado = valorVeiculo - valorEntrada;
        double taxaMensal = 0.0149; // 1,49% a.m.

        // Fórmula Price (Sistema Francês de Amortização)
        double parcela = valorFinanciado
                * (taxaMensal * Math.pow(1 + taxaMensal, numeroParcelas))
                / (Math.pow(1 + taxaMensal, numeroParcelas) - 1);

        double totalPago = valorEntrada + (parcela * numeroParcelas);
        double custoTotal = totalPago - valorVeiculo;
        double cetAnual = Math.pow(1 + taxaMensal, 12) - 1;

        return String.format("""
                        💰 Simulação de Financiamento — AutoSeminovos
                        
                        Valor do veículo:    R$ %,.2f
                        Entrada:             R$ %,.2f (%.1f%%)
                        Valor financiado:    R$ %,.2f
                        Parcelas:            %d x R$ %,.2f
                        Total a pagar:       R$ %,.2f
                        Custo do crédito:    R$ %,.2f
                        Taxa mensal:         1,49%% a.m.
                        CET estimado:        %.2f%% a.a.
                        
                        *Simulação com base na Tabela Price. Valores sujeitos a análise de crédito.
                        """,
                valorVeiculo, valorEntrada, (valorEntrada / valorVeiculo) * 100,
                valorFinanciado, numeroParcelas, parcela,
                totalPago, custoTotal, cetAnual * 100
        );
    }

    // -------------------------------------------------------------------------
    // TOOL 3: Agendamento de Test-Drive
    // -------------------------------------------------------------------------
    @Tool("Agenda um test-drive para o cliente. " +
            "Requer o nome do cliente, o modelo do veículo de interesse e a data desejada no formato dd/MM/yyyy.")
    public String agendarTestDrive(String nomeCliente, String modeloVeiculo, String dataDesejada) {

        System.out.println(">>> TOOL ACIONADA: agendarTestDrive | cliente=" + nomeCliente +
                " | modelo=" + modeloVeiculo +
                " | data=" + dataDesejada);

        // Validação básica de disponibilidade (em produção: consulta banco/calendário)
        boolean modeloDisponivel = ESTOQUE.stream()
                .anyMatch(v -> v.get("modelo").toString()
                        .equalsIgnoreCase(modeloVeiculo));

        if (!modeloDisponivel) {
            return String.format(
                    "Não encontramos o modelo '%s' no estoque atual. " +
                            "Posso verificar outros modelos disponíveis para agendamento.", modeloVeiculo);
        }

        // Protocolo gerado (em produção: persistido no banco)
        String protocolo = "TD-%d-%s".formatted(
                System.currentTimeMillis() % 100000,
                LocalDate.now().getYear()
        );

        return String.format("""
                        ✅ Test-Drive Agendado com Sucesso!
                        
                        Protocolo:   %s
                        Cliente:     %s
                        Veículo:     %s
                        Data:        %s
                        Local:       AutoSeminovos — Av. Principal, 1000
                        Horário:     A confirmar por WhatsApp/e-mail
                        
                        Nossa equipe entrará em contato em até 2 horas úteis para confirmar o horário.
                        """,
                protocolo, nomeCliente, modeloVeiculo, dataDesejada
        );
    }
}