package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;
import br.com.alura.screenmatch.util.EnvUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

//        for (int i = 0; i < dados.totalTemporadas(); i++) {
//            List<DadosEpisodio> episodiosTemporada = dadosTemporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporada.size(); j++) {
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }
        dadosTemporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
    }
}
