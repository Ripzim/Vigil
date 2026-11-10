package app.vigil.feature.devotion

/**
 * Conteúdo devocional embutido no app.
 *
 * Decisão editorial: o app NÃO embute texto bíblico. Cada leitura traz apenas a referência
 * (livro, capítulo e versículos), o contexto e uma reflexão escrita para o VIGIL.
 * Assim nenhuma citação é reproduzida de memória e nenhuma tradução é atribuída indevidamente:
 * o usuário lê o texto na sua própria Bíblia (ARA, ARC, NVI etc.).
 */
data class DevotionReading(
    val key: String,
    val reference: String,
    val title: String,
    val context: String,
    val reflection: String,
    val prayer: String,
)

object DevotionContent {
    const val TRANSLATION_NOTE =
        "Este app não inclui o texto bíblico. Leia a passagem na sua Bíblia (ARA, ARC, NVI ou outra de sua confiança) " +
            "e use a reflexão abaixo como guia."

    val readings: List<DevotionReading> = listOf(
        DevotionReading(
            key = "rm8_1_4",
            reference = "Romanos 8:1-4",
            title = "Nenhuma condenação em Cristo",
            context = "Paulo conclui a argumentação dos capítulos 1 a 7: a lei não pôde nos justificar, mas Deus o fez em Cristo.",
            reflection = "A obra de Cristo não é um recomeço que você precisa merecer a cada manhã. Ela já foi realizada " +
                "por você, e a sua posição diante de Deus depende dela, não do seu último dia bom ou ruim. " +
                "Isso não anula o combate, mas muda de onde você luta: de quem já foi declarado justo, não de quem tenta se provar.",
            prayer = "Pai, obrigado porque em Cristo não há condenação para mim. Firma meu coração nessa verdade e " +
                "ensina-me a andar segundo o Teu Espírito hoje.",
        ),
        DevotionReading(
            key = "sl51",
            reference = "Salmo 51",
            title = "Misericórdia para quem caiu",
            context = "Salmo de Davi após seu pecado grave, confrontado pelo profeta Natã.",
            reflection = "Davi não minimiza o que fez nem foge para a autojustificação ou para o desespero. " +
                "Ele apela para a misericórdia de Deus e pede um coração limpo, o que só Deus pode dar. " +
                "A confissão honesta é o caminho de volta, e quem o percorre encontra um Deus que restaura, não um juiz que se esquiva.",
            prayer = "Senhor, tem misericórdia de mim conforme a Tua bondade. Cria em mim um coração limpo e renova " +
                "em mim um espírito firme.",
        ),
        DevotionReading(
            key = "1co10_1_13",
            reference = "1 Coríntios 10:1-13",
            title = "Deus é fiel na tentação",
            context = "Paulo adverte a igreja usando o exemplo de Israel no deserto, que caiu por confiar em si mesmo.",
            reflection = "A passagem une duas coisas: a advertência séria contra o excesso de confiança e a promessa de que Deus é fiel. " +
                "A tentação que você enfrenta não é um caso único e fora do alcance da graça. " +
                "Vigiar significa, em prática, buscar a saída que Deus provê e sair por ela cedo, em vez de testar o limite.",
            prayer = "Deus fiel, guarda-me de confiar em mim mesmo. Mostra-me a saída antes que eu precise dela e dá-me " +
                "coragem para tomá-la.",
        ),
        DevotionReading(
            key = "hb4_14_16",
            reference = "Hebreus 4:14-16",
            title = "Um sumo sacerdote que se compadece",
            context = "Hebreus apresenta Cristo como sumo sacerdote superior, que atravessou os céus e foi tentado em tudo como nós, mas sem pecado.",
            reflection = "O trono de Deus é descrito como trono de graça. Você é convidado a se aproximar justamente na fraqueza, " +
                "não depois de se arrumar. Cristo conhece a tentação por dentro e intercede por você. " +
                "Voltar a Ele depois de uma queda não é presunção; é o que o texto manda fazer.",
            prayer = "Senhor Jesus, obrigado porque entendes minha fraqueza. Aproximo-me de Ti com confiança para " +
                "receber misericórdia e graça no momento certo.",
        ),
        DevotionReading(
            key = "1jo1_5_2_2",
            reference = "1 João 1:5 a 2:2",
            title = "Luz, confissão e Advogado",
            context = "João escreve para que os crentes não pequem, e ao mesmo tempo lhes dá segurança quando pecam.",
            reflection = "Andar na luz inclui admitir a escuridão. Esconder não protege; confessar é o que a passagem associa ao perdão. " +
                "Note o fim do trecho: se alguém pecar, há um Advogado junto ao Pai, Jesus Cristo, o justo. " +
                "Sua esperança está em quem Ele é e no que fez, não na ausência de falhas.",
            prayer = "Pai, trago à luz o que está escondido. Obrigado porque Jesus Cristo é meu Advogado e a propiciação " +
                "pelos meus pecados.",
        ),
        DevotionReading(
            key = "sl130",
            reference = "Salmo 130",
            title = "Das profundezas",
            context = "Um dos salmos de peregrinação (cânticos de romagem), clamor de quem espera no Senhor.",
            reflection = "O salmista clama do fundo e não do alto. Ele reconhece que, se Deus registrasse as iniquidades, ninguém subsistiria, " +
                "e fundamenta a esperança no perdão que existe em Deus. Esperar no Senhor é uma vigília ativa: " +
                "a sentinela aguarda a manhã com a certeza de que ela vem.",
            prayer = "Senhor, das profundezas clamo a Ti. Espero em Ti, e na Tua palavra ponho a minha esperança.",
        ),
        DevotionReading(
            key = "ef2_1_10",
            reference = "Efésios 2:1-10",
            title = "Salvos pela graça, mediante a fé",
            context = "Paulo descreve a condição humana sem Cristo e a iniciativa de Deus em salvar (sola gratia, sola fide).",
            reflection = "O texto parte de um diagnóstico realista (mortos em delitos e pecados) e chega a uma resposta que vem inteira de Deus. " +
                "A salvação não é fruto de obras para que ninguém se glorie, mas produz obras que Deus preparou de antemão. " +
                "Disciplina cristã é resposta à graça recebida, não tentativa de comprá-la.",
            prayer = "Pai, obrigado porque a salvação é dom Teu. Que as minhas escolhas de hoje sejam fruto de gratidão, " +
                "não de medo.",
        ),
        DevotionReading(
            key = "gl5_13_26",
            reference = "Gálatas 5:13-26",
            title = "Liberdade e o fruto do Espírito",
            context = "Paulo defende a liberdade cristã contra o legalismo, sem deixar que ela vire pretexto para a carne.",
            reflection = "Há uma liberdade que serve ao amor e outra que serve aos desejos. A passagem não promete ausência de conflito: " +
                "descreve carne e Espírito em luta. A saída que ela aponta é andar no Espírito e produzir o Seu fruto, " +
                "inclusive o domínio próprio, o que é diferente de apenas suprimir o desejo à força.",
            prayer = "Espírito Santo, guia meus passos hoje. Produz em mim amor, alegria, paz e domínio próprio.",
        ),
        DevotionReading(
            key = "pv4_20_27",
            reference = "Provérbios 4:20-27",
            title = "Guarda o teu coração",
            context = "Um pai ensina ao filho o valor de atentar à sabedoria, e a guardar o coração, a boca e os olhos.",
            reflection = "O texto trata a vigilância como cuidado, não como paranoia: o coração é fonte da vida e merece ser guardado com todo zelo. " +
                "Em termos práticos, trata dos olhos, dos passos e dos caminhos. Escolher o caminho com antecedência é mais sábio do que depender da força de vontade no meio da tentação.",
            prayer = "Senhor, dá-me sabedoria para guardar meu coração, meus olhos e meus caminhos, e firma os meus passos.",
        ),
        DevotionReading(
            key = "tt2_11_14",
            reference = "Tito 2:11-14",
            title = "A graça que ensina",
            context = "Paulo orienta Tito sobre a vida da igreja, tendo como base a manifestação da graça de Deus.",
            reflection = "A mesma graça que salva também ensina: renunciar à impiedade e viver de modo sóbrio, justo e piedoso. " +
                "A mudança de vida é descrita como consequência da redenção que Cristo operou, que se deu a Si mesmo para nos remir " +
                "e purificar um povo para Si. A motivação é a esperança da Sua vinda, não a vergonha.",
            prayer = "Senhor, ensina-me pela Tua graça a renunciar ao que Te desonra e a viver com esperança na Tua vinda.",
        ),
        DevotionReading(
            key = "fp1_3_11",
            reference = "Filipenses 1:3-11",
            title = "Quem começou, completará",
            context = "Paulo agradece a Deus pela igreja de Filipos e ora para que o amor deles cresça em conhecimento e discernimento.",
            reflection = "A confiança de Paulo não repousa na constância dos filipenses, mas em Deus, que começou a boa obra e a levará a termo. " +
                "A perseverança, então, não é uma corrida solitária: você persevera porque está sendo sustentado. " +
                "Recomeçar hoje é parte do processo, não sinal de que ele falhou.",
            prayer = "Pai, obrigado porque a obra que começaste em mim Tu mesmo completarás. Faze crescer o meu amor com discernimento.",
        ),
    )

    fun byKey(key: String): DevotionReading? = readings.firstOrNull { it.key == key }
}
