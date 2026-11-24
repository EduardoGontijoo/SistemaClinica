# Sistema de Gerenciamento de Clínica

## Identificação

- **Aluno:** Eduardo Gontijo
- **Disciplina:** Estrutura de Dados 2
- **Professora:** Karla

## 1. Sobre o projeto

O Sistema de Gerenciamento de Clínica é uma aplicação desktop desenvolvida em Java, com interface gráfica JavaFX e banco de dados MySQL.

O objetivo do projeto é organizar o atendimento de uma clínica médica, permitindo o cadastro de médicos e pacientes, a emissão de senhas, o gerenciamento das filas de espera e o acompanhamento dos atendimentos.

## 2. Funcionalidades

- Login de administrador, médicos e pacientes.
- Cadastro de médicos e associação com especialidades e consultórios.
- Cadastro de pacientes.
- Emissão de senhas normais e prioritárias.
- Organização da fila conforme as regras de prioridade.
- Consulta da fila de espera por especialidade.
- Chamada do próximo paciente pelo médico.
- Finalização e registro dos atendimentos.
- Consulta ao histórico de atendimentos.
- Armazenamento das informações no MySQL.

## 3. Tipos de usuários

**Administrador:** responsável pelo gerenciamento dos médicos e consultórios.

**Médico:** pode visualizar a fila de sua especialidade, chamar pacientes, finalizar atendimentos e consultar o histórico.

**Paciente:** pode realizar seu cadastro e solicitar uma senha para atendimento.

## 4. Especialidades disponíveis

O sistema possui cinco especialidades:

- Clínica Geral
- Pediatria
- Ginecologia
- Cardiologia
- Psiquiatria

Cada especialidade pode possuir até três médicos, com consultórios associados.

## 5. Regras de prioridade

O sistema organiza os pacientes de acordo com a seguinte ordem de prioridade:

1. Idosos com 80 anos ou mais.
2. Idosos com 60 anos ou mais.
3. Pessoas com deficiência (PCD).
4. Gestantes.
5. Pessoas com obesidade.
6. Crianças de colo.
7. Atendimento normal.

Quando existem pacientes de diferentes categorias na fila, o sistema considera essa ordem para realizar as chamadas. Pacientes sem prioridade são atendidos depois dos prioritários.

## 6. Estruturas de dados utilizadas

Durante o desenvolvimento foram utilizadas estruturas de dados da linguagem Java para organizar e manipular as informações.

- **ArrayList:** utilizado para armazenar e percorrer coleções de elementos.
- **List:** utilizado para representar listas de informações, como registros e atendimentos.
- **Map:** utilizado para relacionar informações por meio de chaves e valores.
- **LinkedHashMap:** utilizado para armazenar informações mantendo a ordem de inserção.
- **HashMap:** utilizado para armazenar associações entre elementos, como especialidades e índices.

Essas estruturas auxiliam no gerenciamento dos médicos, pacientes, senhas e atendimentos.

## 7. Tecnologias utilizadas

- Java
- JavaFX
- MySQL
- JDBC
- Maven
- IntelliJ IDEA
- Git e GitHub

## 8. Banco de dados

O projeto utiliza o MySQL para armazenar os dados da aplicação.

As principais tabelas são:

- `medicos`: armazena os dados dos médicos.
- `pacientes`: armazena os dados dos pacientes.
- `senhas`: registra as senhas emitidas e seus respectivos status.
- `atendimentos`: registra os atendimentos realizados.

O arquivo `sistema_clinica.sql`, disponível na raiz do projeto, contém o script de criação do banco de dados e das tabelas.

## 9. Como executar o projeto

### Pré-requisitos

- JDK compatível com a versão configurada no projeto.
- IntelliJ IDEA.
- Maven.
- MySQL Server.

### Instalação

1. Clone o repositório do GitHub.
2. Abra o projeto no IntelliJ IDEA.
3. Configure o MySQL.
4. Execute o script `sistema_clinica.sql` para criar o banco e as tabelas, caso esteja configurando uma instalação nova.
5. Configure os dados de conexão com o banco no arquivo `ConexaoMySQL.java`.
6. Aguarde o Maven carregar as dependências.

### Execução

No terminal do IntelliJ, execute:

```bash
mvn javafx:run
```

A aplicação será iniciada pela tela de login.

## 10. Imagens do sistema

As imagens das telas da aplicação serão adicionadas nesta seção.

Sugestão de imagens:

- Tela de login.
- Tela do administrador.
- Tela de cadastro de pacientes.
- Tela do médico com a fila de espera.
- Tela de histórico de atendimentos.

## 11. Autor

**Eduardo Gontijo**

