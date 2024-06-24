# SmartFactory API
#### built with https://readme.so/
## Configuración

- SDK corretto 21.0.3
![img.png](readme1.png)

## API Docs
[Swagger - OpenAPI 3.0](http://localhost:8080/swagger-ui/index.html)

## API Reference - Tizada WIP

#### Get all items

```http
  POST /api/tizada
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `api_key` | `string` | **Required**. Your API key |

#### Get item

```http
  GET /api/tizadas/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of item to fetch |

## How to

- Instalar dependencias https://www.youtube.com/watch?v=FWPk0aD3fYk
