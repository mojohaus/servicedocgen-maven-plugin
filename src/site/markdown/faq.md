title: Frequently Asked Questions

# Frequently Asked Questions

**General**

1. [Can you also generate documentation from JAX-WS web-services?](#jax-ws)
2. [Can you also generate swagger.json?](#swagger-json)

## General

<a id="jax-ws"></a>

### Can you also generate documentation from JAX-WS web-services?

No. Not yet. Please get involved and create a pull-request if you want to have this.

<a id="swagger-json"></a>

### Can you also generate swagger.json?

Not as `swagger.json`, which is the Swagger 2 format. The plugin writes an OpenAPI 3 description as `OpenApi.yaml`
and a Swagger UI page, `SwaggerUI.html`, next to the HTML documentation in `target/site/servicedoc`. Please get
involved and create a pull-request if you want to have more.
