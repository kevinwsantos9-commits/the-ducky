# Duck Power (Fabric 1.21.1)

## Etapa 1 - Base
- Aba criativa "Duck Power"
- Item "Boné de Pato" (duck_cap) com 2 ícones: normal e awakening
- Componentes de dados: `awakened` e `awaken_end_tick`

## Como rodar
1. JDK 21 + IntelliJ IDEA (plugin Minecraft Development ajuda).
2. Abra a pasta como projeto Gradle. Se faltar o gradle wrapper, gere um projeto vazio em
   https://fabricmc.net/develop/template e copie a pasta `gradle/`, `gradlew` e `gradlew.bat` para cá.
3. `./gradlew runClient`
4. Modo criativo -> aba "Duck Power".
5. Teste o ícone awakening: `/give @s duck_power:duck_cap[duck_power:awakened=true]`

## Etapa 2 - Modelo 3D
- `bone_pato.obj` convertido para `models/item/duck_cap_base.json` (sem cabeça/mão de referência)
- Texturas 64x64: `pato_normal.png` e `pato_awakening.png` (troca via predicado `awakened`)
- Ângulos snapados para 22.5° (limite do Minecraft): tufo 5°->0, tufos ±30°->±22.5°, bico -10°/-6°->0
- Ajuste `display` do JSON se a posição na mão/GUI ficar torta

## Etapa 3 - Awakening (esta)
- Botão direito no boné = fúria de 2 min (Força II, Velocidade II, Resistência I, Regeneração I), depois volta ao normal
- Recarga de 3 min a partir da ativação; ícone troca para awakening e ganha brilho
- Poderes da fúria ficam em `power/FuryPowers.java` (lista editável, usada pelo menu da Etapa 5)

## Próximas etapas
4. Animação (implementada nesta versão)
- Ao usar o boné, ele sobe acima do jogador, gira 360° e desce até a cabeça.
- Duração visual aproximada: 2,4 segundos; movimento com subida e descida suavizadas.
- A animação é local (cliente); a ativação dos poderes continua autoritativa no servidor.
5. Menu de poderes


## Etapa 4 - Como testar
1. Entre no mundo com o boné na mão.
2. Clique com o botão direito.
3. O boné deve aparecer voando acima da cabeça, girar uma volta e descer.
4. O Awakening e seus efeitos são ativados pelo servidor e continuam durando 2 minutos.

Observação: esta etapa adiciona a animação visual. O boné continua sendo usado como item na mão; a etapa seguinte pode implementar o menu e, se desejado, a lógica para equipá-lo automaticamente no slot da cabeça.


## Etapa 5 - Menu Duck Power
- Segure o boné na mão principal ou secundária e pressione **O**.
- O menu mostra oito poderes com botões ON/OFF.
- Força, Velocidade, Resistência e Regeneração começam ligados.
- Super salto, Resistência ao fogo, Visão noturna e Pressa começam desligados.
- As escolhas controlam os efeitos aplicados quando o Awakening é ativado.
- O menu não pausa o mundo.

**Limitação desta etapa:** as opções ficam na memória do jogo e voltam ao padrão ao reiniciar. Em mundo single-player/integrado, a seleção é compartilhada com a lógica do servidor. Sincronização de configurações para servidores dedicados e salvamento permanente serão feitos na próxima etapa.


## Etapa 6 - Níveis e salvamento da configuração
- Cada boné guarda sua própria configuração no componente `duck_power:power_config`.
- No menu, cada botão percorre OFF -> nível 1 -> nível 2 -> nível 3 -> OFF.
- Clique em **Salvar e fechar** para enviar a configuração ao servidor.
- A ativação do Awakening lê os níveis gravados no boné.
- Ao terminar os 2 minutos, os efeitos de status gerenciados pelo Duck Power são removidos.
- Os níveis padrão são Força 2, Velocidade 2, Resistência 1 e Regeneração 1; os demais começam desligados.

**Atenção:** esta etapa ainda precisa de compilação e teste em jogo. A configuração é enviada ao servidor quando o boné está na mão principal ou secundária. O servidor só aceita a configuração se o boné estiver em uma dessas mãos. Teste primeiro em mundo de cópia.


## Etapa 7 - Aura e HUD do Awakening
- Enquanto o boné estiver desperto, surgem pequenos pulsos de partículas Soul Fire Flame e End Rod ao redor do jogador.
- Uma HUD discreta mostra `DUCK AWAKENING` e a contagem regressiva MM:SS.
- A HUD procura o boné na mão principal, mão secundária e inventário.
- As partículas são geradas no servidor em intervalos de 5 ticks para reduzir custo.
- Ao acabar o tempo, a rotina de encerramento remove os efeitos e desliga o estado desperto.

**Ainda pendente:** brilho real dos olhos do modelo do jogador e animações corporais exigem renderização/modelo de jogador customizado. Esta etapa não altera a skin do jogador.
