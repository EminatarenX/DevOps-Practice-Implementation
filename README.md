# DevOps Implementation Guide

## Puesta en marcha (Kind)

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
> **¿Por qué la diferencia?** En Linux con versiones recientes de Docker Desktop, `buildx` genera por defecto un *OCI image index* con manifests de *provenance/SBOM* (attestations). Ese formato no es resuelto correctamente por el `kubelet` de `kindest/node:v1.35.x` cuando la imagen se carga vía `kind load docker-image`, y se manifiesta como `ImagePullBackOff` o `ErrImageNeverPull` aunque la imagen sí esté en el nodo. Las flags `--provenance=false --sbom=false --output type=docker` fuerzan una imagen plana (`manifest.v1`) compatible.


### Crear el cluster

```bash
kind create cluster --config manifest/cluster-config/config.yaml
```

### Aplicar manifiestos (orden recomendado)

Primero aplica los manifiestos de **configuración** (ConfigMaps/Secrets) y después los de **workloads** (Deployments/Services).

Ejemplo para `users-service`:

```bash
kubectl apply -f manifest/users-service/00-config.yaml
# si tienes secrets separados, aplícalos aquí también
kubectl apply -f manifest/users-service/01-deployment.yaml
kubectl apply -f manifest/users-service/02-service.yaml
```

## Ingress (ingress-nginx con Helm)

### Paso 1 — Instalar Helm

Verifica:

```bash
helm version
```

Si no existe:

```bash
brew install helm
```

### Paso 2 — Agregar repo de ingress-nginx

```bash
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
```

### Paso 3 — Instalar el ingress controller

En Kind, instala el controller con Helm así:

```bash
helm install ingress-nginx ingress-nginx/ingress-nginx \
  --namespace ingress-nginx \
  --create-namespace
```

### Paso 4 — Verificar instalación

```bash
kubectl get pods -n ingress-nginx
```

Deberías ver `ingress-nginx-controller` en estado `Running`.

### Paso 5 — Crear y aplicar el Ingress

El manifiesto del repo está en `manifest/k8s/ingress.yaml` y usa el host `users.emicorp.local` apuntando al Service `users-service` (puerto 8080).

Aplica:

```bash
kubectl apply -f manifest/k8s/ingress.yaml
```

### Paso 6 — Configurar DNS local (hosts)

Edita `/etc/hosts` y agrega:

```text
127.0.0.1 users.emicorp.local
```

### Paso 7 — Probar

Abre:

`http://users.emicorp.local`

