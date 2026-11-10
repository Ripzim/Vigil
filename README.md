# VIGIL 0.3.0 — Android Foundation

> Vigie. Resista. Persevere.

App Android nativo (Kotlin, Jetpack Compose, Material 3) de disciplina digital e recuperação, com identidade cristã reformada. Local-first: sem contas, telemetria, anúncios ou servidores.

## Estado desta entrega (Fase 1)

**Importante:** este código foi escrito sem acesso a Android SDK nem a repositórios Maven/Gradle (o ambiente de desenvolvimento bloqueava essas redes). **Ele ainda não foi compilado nem testado.** A primeira compilação real acontece no GitHub Actions; erros de build, se houver, aparecerão lá e precisam ser corrigidos antes de considerar a Fase 1 concluída.

| Módulo | Estado |
|---|---|
| Tema AMOLED, navegação, dashboard simples/avançado | Escrito, não compilado |
| Room (8 entidades, DAOs, repositories, esquema de migrações) | Escrito, não compilado |
| DataStore (configurações) | Escrito, não compilado |
| Shield: CRUD de listas de bloqueio/permissão com validação de domínio | Escrito. **Não bloqueia nada** (filtro DNS é a Fase 2) |
| Watch: pausa consciente manual com registro | Escrito. Alertas e notificações: Fase 3 |
| Recovery: registro de vontades/recaídas, gatilhos, resumo local | Escrito |
| Devotion: 11 leituras (referência, contexto, reflexão, oração), favoritos, histórico | Escrito. **Sem texto bíblico embutido**, por decisão editorial |
| Intelligence, Vision | Apenas tela de status. Não implementados |
| Configurações: modo avançado, apagar todos os dados | Escrito |
| Senha, textos de recuperação, VpnService, Keystore | Não iniciados (Fases 2 e 3) |

Testes unitários escritos (não executados): `DomainNormalizerTest`, `DevotionContentTest`.

## Arquitetura

- Módulo Gradle único `:app`, com modularidade por pacote (`feature/*`, `core/*`).
- MVVM: Compose → ViewModel → Repository → Room/DataStore. DI manual (`AppContainer`).
- Configurações no DataStore (não há tabela Room de configurações).
- Nenhuma permissão Android declarada nesta versão. Backup do Android desativado.
- Limite conhecido: o banco ainda não é criptografado em repouso.

## Como gerar e instalar o APK (sem Android Studio)

1. Crie um repositório no GitHub e envie esta pasta (branch `main`).
2. Abra a aba **Actions**. O workflow **Build APK (debug)** roda sozinho a cada push (ou use **Run workflow**).
3. Quando terminar com sucesso, abra a execução e baixe o artefato **vigil-debug-apk** (um `.zip`).
4. Extraia o `.zip` no celular ou no computador e copie o `.apk` para o Galaxy A23 5G.
5. No Android, abra o `.apk` e permita "Instalar apps desconhecidos" para o app usado (Arquivos, Chrome etc.).
6. O app aparece como **VIGIL** (id de debug: `app.vigil.debug`).

Se o workflow falhar, abra o log do passo que falhou e envie o erro para correção.

## Próximas fases

2. Protection (VpnService + filtro DNS, senha, Keystore) · 3. Recovery (Watch com notificações, textos de recuperação e similaridade) · 4. Intelligence/Vision · 5. Integração e testes.
