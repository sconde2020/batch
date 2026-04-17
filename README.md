# Spring Batch retry/skip/restart demo

Ce projet illustre :
- Retry automatique sur erreur transiente.
- Skip des erreurs de donnees invalides.
- Reprise (restart) d'un job echoue.
- Transactions par chunk.
- Monitoring via les tables Spring Batch en H2.

## Points cle

- Retry : `TransientPersonException` (3 tentatives).
- Skip : `InvalidPersonException` (jusqu'a 5 items).
- Restart : lancer avec `--restart=<executionId>` pour relancer une execution precise.
- Chunk transactionnel : `app.batch.chunk-size` (defaut 10).

## Lancer

```powershell
./mvnw spring-boot:run
```

## Relancer un job en echec

```powershell
./mvnw spring-boot:run -Dspring-boot.run.arguments="--restart=<executionId>"
```

## Monitoring (H2)

- Console H2 active par defaut.
- Tables Batch : `BATCH_JOB_INSTANCE`, `BATCH_JOB_EXECUTION`, `BATCH_STEP_EXECUTION`.

URL typique :
- `http://localhost:8080/h2-console`

JDBC URL :
- `jdbc:h2:mem:testdb`
