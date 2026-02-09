# 📦 VirtualChest - Minecraft 1.21.1

Plugin de baús virtuais privados para servidores Minecraft, desenvolvido com foco em performance e suporte multilíngue.

## ✨ Funcionalidades
- **Múltiplos Baús**: Acesso via `/pv <numero>`.
- **Multilanguage**: Suporte para Português (PT) e Inglês (EN) via `config.yml`.
- **Sistema de Admin**: Administradores podem inspecionar baús de outros jogadores.
- **Sons Customizados**: Efeitos sonoros ao abrir os baús.
- **Persistência**: Salvamento automático de itens em arquivos `.yml` individuais.

## 🛠️ Comandos e Permissões
| Comando | Descrição | Permissão |
| :--- | :--- | :--- |
| `/pv <id>` | Abre um baú específico | `virtualchest.pv.<id>` |
| `/pv admin <player> <id>` | Inspeciona baú de terceiros | `virtualchest.admin` |

## 🚀 Como Compilar
Este projeto utiliza o **Maven**. Para gerar o arquivo `.jar`:
1. Clone o repositório.
2. Execute `mvn clean package` no terminal.
3. O arquivo final estará na pasta `target`.
