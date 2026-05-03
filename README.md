# Consultas CEP API

![Coverage](https://img.shields.io/badge/coverage-97%25-brightgreen)
![Java](https://img.shields.io/badge/java-25-blue)
![Spring Boot](https://img.shields.io/badge/spring--boot-4.0.6-6DB33F)
![PostgreSQL](https://img.shields.io/badge/postgresql-15-336791)
![H2](https://img.shields.io/badge/h2-tests%20in--memory-1f6feb)

API para consulta de CEP com persistencia em PostgreSQL, cache em Redis e suporte a provedor mockado (WireMock) ou API real do ViaCEP.

## Setup rapido

1. Copie o arquivo de ambiente:

```bash
cp .env.example .env
```

2. Se quiser, ajuste variaveis no `.env`.

3. Suba o ambiente:

```bash
docker compose up -d --build
```

4. Acesse:
- Swagger UI: [http://localhost:8080/api/v1/swagger-ui.html](http://localhost:8080/api/v1/swagger-ui.html)
- API Docs JSON: [http://localhost:8080/api/v1/v3/api-docs](http://localhost:8080/api/v1/v3/api-docs)

Nota: se voce mudar a porta da aplicacao, ajuste os links para localhost usando a nova porta. A porta padrao atual e 8080.

Se alterar qualquer variavel no `.env`, faca rebuild:

```bash
docker compose up -d --build
```

## Configuracao por .env

O [compose.yaml](compose.yaml) ja usa env_file: .env no servico da aplicacao.

Fluxo recomendado:
1. Copie [.env.example](.env.example) para .env.
2. Sempre que precisar mudar configuracao, altere o arquivo .env gerado (nao o .env.example).
3. Aplique as mudancas com rebuild:

```bash
docker compose up -d --build
```

## Provedor de CEP: mock ou real

No `application.properties`, a base esta preparada assim:

```properties
viacep.url=${VIACEP_URL:http://localhost:8081/ws}
```

- Mock (padrao em Docker): mantenha `VIACEP_URL=http://wiremock:8080/ws`
- API real: troque para `VIACEP_URL=https://viacep.com.br/ws`

Ou seja, e possivel testar com mock para fluxo controlado e, quando quiser, mudar para a API real sem alterar codigo, so via `.env`.

Os mocks estao em `wiremock/mappings/cep-mappings.json`.

## CEPs disponiveis no mock

- `01001000`
- `20040020`
- `30130110`
- `99999999` (retorna `{"erro": true}`)

## Cache Redis (30 minutos)

O cache padrao esta em meia hora:

```properties
cep.cache.ttl-seconds=${CEP_CACHE_TTL_SECONDS:1800}
```

Racional: em cenario real (fora de mock), reduz chamadas repetidas ao mesmo provedor em curto intervalo e evita sobrecarga desnecessaria.

## Cobertura de testes (JaCoCo)

Para testes unitarios, foi usado H2 em memoria (profile de teste) para execucao mais rapida e isolada.

Gerar cobertura:

```bash
./mvnw clean verify
```

Relatorio HTML:
- arquivo: `target/site/jacoco/index.html`
- opcional: abrir com Live Server para navegar na interface de cobertura

Comando util para abrir no VS Code (se tiver a extensao Live Server):
1. Abra `target/site/jacoco/index.html`
2. Use a acao `Open with Live Server`
