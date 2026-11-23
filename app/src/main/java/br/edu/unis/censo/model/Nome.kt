package br.edu.unis.censo.model

/** Um nome (ou sobrenome) e quantas pessoas o possuem na localidade. */
data class NomeFrequencia(
    val nome: String,
    val frequencia: Long
)

/** Resposta da API de Nomes (Censo 2022) para uma localidade. */
data class RankingNomes(
    val localidade: Int,
    val mulheres: List<NomeFrequencia>,
    val homens: List<NomeFrequencia>,
    val sobrenomes: List<NomeFrequencia>
)
