# E-commerce Microservices — Spring Boot + MySQL + Docker + Kubernetes + AWS

## Included
- API Gateway (8080)
- Product Service (8081), User Service (8082), Order Service (8083)
- Separate MySQL databases for each service
- REST CRUD APIs and order-to-product REST communication
- JUnit 5 / MockMvc controller tests
- Dockerfiles and Docker Compose
- Kubernetes Deployments and Services with health probes and resource limits
- AWS ECR/EKS/RDS deployment guide
- Postman collection

## Important production note
This is a runnable learning/portfolio foundation, not a fully audited production commerce platform. Before handling real customers or payments, add OIDC/JWT authentication and authorization, payment-provider integration, atomic inventory reservation with a saga/outbox, idempotency, rate limiting, TLS/WAF, Flyway/Liquibase migrations, centralized secrets, observability, backups/restore drills, security scans, load tests and penetration tests. Local credentials are development-only. Kubernetes assumes managed external MySQL. AWS resources are not created by this ZIP and may incur charges.

## Requirements
JDK 17, Maven 3.9+, Docker Desktop with Compose, STS. AWS deployment also requires AWS CLI v2, kubectl, eksctl, an AWS account and suitable IAM permissions.

## Import into STS
1. Extract the ZIP.
2. Open STS → File → Import → Maven → Existing Maven Projects.
3. Select the extracted `ecommerce-microservices` folder.
4. Select the root `pom.xml`, then Finish.
5. Wait for Maven dependencies; confirm Java 17 is configured.

## Run locally
From the project root:
```bash
docker compose up --build
```
This starts three MySQL databases, three services and the gateway.
- Gateway health: http://localhost:8080/actuator/health
- Products: http://localhost:8080/api/products
- Users: http://localhost:8080/api/users
- Orders: http://localhost:8080/api/orders

Stop:
```bash
docker compose down
```
Remove local DB volumes (destructive):
```bash
docker compose down -v
```

## Automated tests
```bash
mvn clean verify
```
Controller tests use mocked repositories and do not require MySQL.

## API examples
Create product:
```http
POST http://localhost:8080/api/products
Content-Type: application/json

{"name":"Wireless Mouse","description":"Bluetooth mouse","price":19.99,"stock":50}
```
Create user:
```http
POST http://localhost:8080/api/users
Content-Type: application/json

{"fullName":"Asha Kumar","email":"asha@example.com","phone":"9876543210"}
```
Create order after both records exist:
```http
POST http://localhost:8080/api/orders
Content-Type: application/json

{"customerId":1,"productId":1,"quantity":2}
```
Import `postman/ecommerce.postman_collection.json` into Postman for sample requests.

**Inventory limitation:** order creation checks stock but does not atomically reserve/decrement it. Implement a reservation/saga workflow before real sales.

## Build Docker images manually
Dockerfiles use the repository root as build context:
```bash
docker build -f product-service/Dockerfile -t ecommerce/product-service:1.0.0 .
docker build -f user-service/Dockerfile -t ecommerce/user-service:1.0.0 .
docker build -f order-service/Dockerfile -t ecommerce/order-service:1.0.0 .
docker build -f api-gateway/Dockerfile -t ecommerce/api-gateway:1.0.0 .
```

## Kubernetes
1. Provision three MySQL schemas on managed MySQL (recommended: Amazon RDS): `product_db`, `user_db`, `order_db`. Keep databases private.
2. Apply namespace:
```bash
kubectl apply -f kubernetes/namespace.yaml
```
3. Edit `kubernetes/configmap.yaml`, replacing the RDS endpoint placeholders.
4. Copy `kubernetes/secret.example.yaml` to `kubernetes/secret.yaml`; replace credentials. Never commit `secret.yaml`. For production, use AWS Secrets Manager with External Secrets or Secrets Store CSI Driver.
5. Push images to ECR and replace `YOUR_ECR_REGISTRY` in manifests with the real image registry.
6. Apply:
```bash
kubectl apply -f kubernetes/configmap.yaml
kubectl apply -f kubernetes/secret.yaml
kubectl apply -f kubernetes/product-service.yaml
kubectl apply -f kubernetes/user-service.yaml
kubectl apply -f kubernetes/order-service.yaml
kubectl apply -f kubernetes/api-gateway.yaml
kubectl -n ecommerce get pods,svc
```
Configure TLS and an AWS Load Balancer Controller/ALB Ingress before public production use. Configure metrics-server before adding HPA autoscaling.

## AWS EKS + ECR outline
These commands create billable resources. Review cost, region, security and quotas first. Example region: `ap-south-1`.

1. Configure AWS CLI:
```bash
aws configure
aws sts get-caller-identity
export AWS_REGION=ap-south-1
export AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
export ECR=$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com
```
2. Create ECR repositories and authenticate:
```bash
for svc in product-service user-service order-service api-gateway; do
  aws ecr create-repository --repository-name ecommerce/$svc --image-scanning-configuration scanOnPush=true --region $AWS_REGION
done
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR
```
3. Build and push images:
```bash
for svc in product-service user-service order-service api-gateway; do
  docker build -f $svc/Dockerfile -t $ECR/ecommerce/$svc:1.0.0 .
  docker push $ECR/ecommerce/$svc:1.0.0
done
```
4. Create an EKS cluster (example learning configuration):
```bash
eksctl create cluster --name ecommerce-cluster --region $AWS_REGION --nodes 2 --nodes-min 2 --nodes-max 4 --node-type t3.medium
aws eks update-kubeconfig --region $AWS_REGION --name ecommerce-cluster
kubectl get nodes
```
5. Create RDS MySQL databases in private subnets with encryption and automated backups. Permit port 3306 only from EKS security groups. Store credentials in Secrets Manager and set JDBC endpoints in the Kubernetes ConfigMap.
6. Replace image references in Kubernetes manifests with `$ECR/ecommerce/<service>:1.0.0`, then apply the manifests.
7. Configure ALB Ingress, ACM TLS, Route 53 DNS, AWS WAF, CloudWatch logs/metrics and alerting for production.

## CI/CD recommendation
Use GitHub Actions with AWS OIDC federation (avoid long-lived AWS access keys). Pipeline: Maven tests → dependency/security scan → image build → ECR push using immutable commit tags → staging deployment → smoke tests → approval → production rollout.

## Production checklist
- [ ] OIDC/JWT authentication and authorization
- [ ] Payment provider; never store raw card data
- [ ] Atomic stock reservation and order saga/outbox
- [ ] Flyway/Liquibase migrations; `ddl-auto=validate`
- [ ] Secrets Manager/KMS and rotation
- [ ] TLS, WAF, rate limits and CORS policy
- [ ] Centralized logs, metrics, traces and alerts
- [ ] Encrypted DB backups and restore drills
- [ ] Container scanning, SBOM and least-privilege IAM
- [ ] Load, security and failure-recovery tests
