package br.edu.unis.censo.model

/** Estado (UF) retornado pela API de Localidades do IBGE. */
data class Estado(
    val id: Int,
    val sigla: String,
    val nome: String
)

/** Município retornado pela API de Localidades do IBGE. */
data class Municipio(
    val id: Int,
    val nome: String
)
