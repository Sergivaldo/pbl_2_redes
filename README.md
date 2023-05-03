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
- Comunicação MQTT para solicitar postos de abastecimento quando sua bateria está menor ou igual a 30% de carga.

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

#### ⬆️ [Voltar ao topo](#inicio)

