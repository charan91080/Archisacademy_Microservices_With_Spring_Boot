# E-commerce Microservices — Spring Boot, MySQL, Docker, Kubernetes, AWS

## Included
- Product Service (8081): product CRUD REST API
- User Service (8082): customer CRUD REST API
- Order Service (8083): creates orders and reads product information over REST
- API Gateway (8080): routes requests to the services
- Separate MySQL databases, JUnit/MockMvc tests, Docker Compose, Kubernetes manifests, Postman collection

## Import into STS
1. Extract this ZIP.
2. Open STS → File → Import → Maven → Existing Maven Projects.
3. Select this extracted folder and import the root `pom.xml`.
4. Set the project JDK to 17 and wait for Maven downloads.

## Run locally
Install Docker Desktop, then from the extracted root:
```bash
docker compose up --build
```
Gateway URL: `http://localhost:8080`
- GET `/api/products`
- GET `/api/users`
- GET `/api/orders`

Stop: `docker compose down`. To delete local DB data too: `docker compose down -v`.

## Run tests
```bash
mvn clean verify
```

## Postman
Import `postman/ecommerce.postman_collection.json`.
Create a product first:
```json
{"name":"Keyboard","description":"Mechanical keyboard","price":49.99,"stock":20}
```
Create a user:
```json
{"fullName":"Asha Kumar","email":"asha@example.com","phone":"9876543210"}
```
Create an order:
```json
{"customerId":1,"productId":1,"quantity":2}
```

## Kubernetes
1. Provision three MySQL schemas on Amazon RDS (or another managed MySQL) and keep the database private.
2. Replace RDS endpoint placeholders in `kubernetes/configmap.yaml`.
3. Copy `kubernetes/secret.example.yaml` to `kubernetes/secret.yaml`, set strong credentials, and never commit that file.
4. Replace example ECR image URIs in the deployment manifests with your own image URIs/tags.
5. Apply:
```bash
kubectl apply -f kubernetes/namespace.yaml
kubectl apply -f kubernetes/configmap.yaml
kubectl apply -f kubernetes/secret.yaml
kubectl apply -f kubernetes/product-service.yaml
kubectl apply -f kubernetes/user-service.yaml
kubectl apply -f kubernetes/order-service.yaml
kubectl apply -f kubernetes/api-gateway.yaml
kubectl apply -f kubernetes/hpa/
```
Install metrics-server for autoscaling. For a production ingress, use AWS Load Balancer Controller, ALB, ACM TLS, and WAF.

## AWS deployment outline (creates billable resources)
Example region: `ap-south-1`. Review pricing, IAM permissions, and AWS quotas first.
1. Configure AWS CLI v2: `aws configure`; verify with `aws sts get-caller-identity`.
2. Create ECR repositories:
```bash
for s in product-service user-service order-service api-gateway; do
  aws ecr create-repository --repository-name ecommerce/$s --image-scanning-configuration scanOnPush=true --region ap-south-1
done
```
3. Login and push images:
```bash
export AWS_REGION=ap-south-1
export AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
export ECR=$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR
for s in product-service user-service order-service api-gateway; do
  docker build -f $s/Dockerfile -t $ECR/ecommerce/$s:1.0 .
  docker push $ECR/ecommerce/$s:1.0
done
```
4. Create EKS (example only; incurs charges):
```bash
eksctl create cluster --name ecommerce-cluster --region $AWS_REGION --nodes 2 --nodes-min 2 --nodes-max 4 --node-type t3.medium
aws eks update-kubeconfig --region $AWS_REGION --name ecommerce-cluster
kubectl get nodes
```
5. Provision private RDS MySQL databases, enable encryption/backups, and allow DB access only from the EKS security group. Use AWS Secrets Manager plus External Secrets/CSI driver for production secrets.
6. Update Kubernetes image references and DB endpoints, then apply manifests.

## Production hardening required
This is a deployable learning foundation, not an audited production commerce platform. Before real customer traffic add OIDC/JWT authentication and authorization, payment-provider integration (never store card data), atomic stock reservation/saga/outbox, idempotency, Flyway/Liquibase migrations, `ddl-auto=validate`, TLS/WAF/rate limiting, secret rotation, structured logs/traces/metrics, alerting, backups/restore drills, image scanning, load tests and security tests. Local credentials are development-only. AWS resources are not created by this ZIP.
