package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;
import br.com.alura.screenmatch.util.EnvUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    Scanner scanner = new Scanner(System.in);
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=" + EnvUtil.getApiKey();
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    public void exibeMenu() {
        System.out.println("Digite o nome da serie:");
        var nomeSerie = scanner.nextLine();

        var endereco = ENDERECO + nomeSerie.replace(" ", "+") + API_KEY;
        var json = consumoAPI.obterDados(endereco);

        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> dadosTemporadas = new ArrayList<>();
        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            endereco = ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY;
            json = consumoAPI.obterDados(endereco);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            dadosTemporadas.add(dadosTemporada);
        }
        dadosTemporadas.forEach(System.out::println);
        System.out.println("\n\n");

//        for (int i = 0; i < dados.totalTemporadas(); i++) {
//            List<DadosEpisodio> episodiosTemporada = dadosTemporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporada.size(); j++) {
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }
        dadosTemporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
        System.out.println("\n\n");

        List<DadosEpisodio> dadosEpisodios = dadosTemporadas
                .stream()
                .flatMap(t -> t.episodios().stream())
                // .toList() imutável
                .collect(Collectors.toList());

//        System.out.println("\nTop 10 episódios: ");
//        var top5 = dadosEpisodios
//                .stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primeiro filtro N/A: " + e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e -> System.out.println("Ordenação: " + e))
//                .limit(10)
//                .peek(e -> System.out.println("Limit: " + e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Map: " + e));
//        top5.forEach(System.out::println);
//        System.out.println("\n\n");

        List<Episodio> episodios = dadosTemporadas
                .stream()
                .flatMap(t -> t.episodios().stream()
                        .map(e -> new Episodio(t.numero(), e)))
//                .sorted(Comparator.comparing(Episodio::getAvaliacao).reversed())
//                .limit(5)
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);
        System.out.println("\n\n");

        Map<Integer, Double> avaliacaoPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada, Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println(avaliacaoPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        System.out.println("média: " + est.getAverage());
        System.out.println("melhor: " + est.getMax());
        System.out.println("pior: " + est.getMin());
        System.out.println("total: " + est.getCount());

//        System.out.println("Informe um trecho do nome do episodio");
//        var trechoDoTitulo = scanner.nextLine();
//
//        var episodio = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoDoTitulo.toUpperCase()))
//                .findFirst();
//
//        if (episodio.isPresent()) {
//            System.out.println("Episódio encontrado: ");
//            System.out.println(episodio.get().getTitulo());
//            System.out.println("Temporada: ");
//            System.out.println(episodio.get().getTemporada());
//        } else {
//            System.out.println("Episódio não encontrado");
//        }
//
//        System.out.println("Informe o ano:");
//        var ano = scanner.nextInt();
//        scanner.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() +
//                                " Episódio: " + e.getTitulo() +
//                                " Data de lançamento: " + e.getDataLancamento().format(formatter)
//                ));
    }
}
