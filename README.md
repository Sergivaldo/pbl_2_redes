<a id="inicio"></a>
## Mi Concorrência e Conectividade - Problema 1

Este documento mostra os detalhes de implementação de um
sistema de carregamento inteligente para veículos elétricos.

O projeto consiste num sistema que indica o melhor posto para um carro de acordo com a carga de bateria deste.
Caso exista um posto em que o carro com sua carga atual de bateria consiga alcançar, este posto será indicado para o carro.

A aplicação utiliza de computação em névoa para trocar informações entre servidores de localizações distintas, permitindo que postos de lugares diferentes
do qual o carro se encontra possam ser informados caso os da região atual do veículo estejam indisponíveis.

O sistema possui os seguintes componentes:

**Carro**
- Possui interface baseada em API REST para consultar melhor posto.
- Comunicação MQTT para solicitar postos de abastecimento quando sua bateria está baixa.

**Posto**
- Comunicação MQTT para transmitir a quantidade de carros em fila.

**Servidor Local**
- Comunicação  MQTT para receber informações dos postos da região.
- Comunicação via socket TCP com o servidor central para solicitar postos de outras regiões que estão disponíveis para um carro.
- Processa o melhor posto da região para um determinado carro.

**Broker**
- Permite que os postos se comuniquem com o servidor local
- Permite que os carros se comuniquem com o servidor local

**Servidor Central**
- Comunicação via socket TCP com os servidores locais para recebimento do estado dos postos e solicitação de postos disponíveis para um carro.
- Processa o melhor posto de várias regiões para um determinado carro.

### Seções 

&nbsp;&nbsp;&nbsp;[**1.** Diagrama do Projeto](#secao1)

&nbsp;&nbsp;&nbsp;[**2.** Carro](#secao2)

&nbsp;&nbsp;&nbsp;[**3.** Posto](#secao3)

&nbsp;&nbsp;&nbsp;[**4.** Servidor Local](#secao4)

&nbsp;&nbsp;&nbsp;[**5.** Servidor Central](#secao5)

### Diagrama do Projeto


![diagrama_redes_2](https://user-images.githubusercontent.com/72475500/235815532-af91ecbd-1f4c-46f0-b7b9-364c36990eeb.png)

O diagrama acima mostra a arquitetura da solução utilizando computação em névoa. Basicamente, o sistema possui vários servidores(névoas) distribuidos em regiões diferentes, cada um destes se conecta a um broker para receber mensagens dos postos e dos carros e também se conectam ao servidor central(nuvem) enviando informações dos postos ou solicitando postos disponíveis a nuvem.

Quando um carro solicita a busca por postos disponíveis, a névoa irá verificar se existe algum na região, caso não tenha, será pedido a nuvem um posto disponível para o veículo pedinte. A nuvem possui uma lista com os postos de todas as regiões, dessa forma quando um posto não é encontrado em uma determinada região, outros podem ser buscados em regiões diferentes.

Essa solução se mostrou eficiente, já que o processamento de informações fica dividido. Em vez de sempre buscar no servidor central um posto, o que acarretaria em uma carga maior para o mesmo, um servidor local pode fazer esse processamento com os postos que tem acesso dessa forma diminuindo o trabalho da nuvem e também o tempo de resposta já que o servidor central processa mais informações(postos de todas as regiões) do que um servidor local.

### Carro

Basicamente, um carro possui uma interface baseada em API REST e a capacidade de se comunicar via protocolo MQTT. O automóvel também possui uma bateria que descarrega em um intervalo de tempo que varia de acordo com a sua taxa de descarga que pode ser lenta(a cada 7 segundos), normal(a cada 5 segundos) ou rápida(a cada 3 segundos).

Caso a carga atual da bateria esteja menor ou igual a 30%, o carro começará a solicitar por postos de abastecimento enviando mensagens que serão capitadas pelo servidor local da região, caso a névoa(servidor local) encontre um posto, este poderá ser visualizado pela interface através da rota `/best_gas_station`. Se nenhum posto for encontrado após a solicitação, será mostrada uma mensagem na mesma rota informando que não há posto disponíveis.

### Posto
O posto é a entidade que possui menos funções, ele somente envia periodicamente informações da sua fila para o servidor local também via protocolo MQTT. Essa fila é atualizada aleatoriamente.

### Servidor Local

### Servidor Central

#### ⬆️ [Voltar ao topo](#inicio)

