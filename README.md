# Description

Example project showcasing invisible problem where with exclusion of both jakarta.annotation and javax.annotation when using Jetty.
The app still runs without any other problems, just `server.servlet.session.timeout` does not have any effect.

There may be some other invisible issues.

# Running

Just run `docker-compose up --build -d` and everything should build automatically.
(You may need to do `export UID` first in your shell.)

Now open `docker-compose exec redis redis-cli monitor` in another terminal and issue login request:

```
curl --basic -u test:secret http://localhost:8080/api/
```

You should be able to quickly see the expiration TTL in redis-cli monitor output.
