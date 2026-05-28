# DevOps Implementation Guide

## Notas y consideraciones

> **⚠️ Build de la imagen: diferencias por arquitectura/host**
>
> El comando de build *no* es el mismo en todos los entornos. Depende de la versión de Docker Desktop / BuildKit y de la arquitectura del host.
>
> **Apple Silicon (M1/M2/M3/M4) — build directo:**
> ```bash
> docker build -t users-service:v1.0 ./users-ms
> ```
>
> **Linux x86_64 (probado en AMD Ryzen 7 7800X3D) — build con flags:**
> ```bash
> docker buildx build \
>   --platform linux/amd64 \
>   --provenance=false --sbom=false \
>   --output type=docker \
>   -t users-service:v1.0 ./users-ms
> ```
>
> **¿Por qué la diferencia?** En Linux con versiones recientes de Docker Desktop, `buildx` genera por defecto un *OCI image index* con manifests de *provenance/SBOM* (attestations). Ese formato no es resuelto correctamente por el `kubelet` de `kindest/node:v1.35.x` cuando la imagen se carga vía `kind load docker-image`, y se manifiesta como `ImagePullBackOff` o `ErrImageNeverPull` aunque la imagen sí esté en el nodo. Las flags `--provenance=false --sbom=false --output type=docker` fuerzan una imagen plana (`manifest.v2`) compatible.
