
# Android Drop da Rave em UI

Android Drop-In da Rave é uma interface pronta para uso que permite aceitar pagamentos com cartão e banco no seu aplicativo Android.

<img alt="Screenshot do Drop-In" src="https://firebasestorage.googleapis.com/v0/b/saveup-9e594.appspot.com/o/Group.png?alt=media&token=e0c89192-b2a4-47e0-a883-3a78005acd2a" width="600"/>

## Antes de você começar
- [Crie suas chaves de teste do ambiente sandbox da Rave](https://flutterwavedevelopers.readme.io/blog/how-to-get-your-staging-keys-from-the-rave-sandbox-environment)
- [Crie suas chaves de teste ao vivo pelo Rave Dashboard](https://flutterwavedevelopers.readme.io/blog/how-to-get-your-live-keys-from-the-rave-dashboard)

## Requerimentos


A versão mínima suportada do SDK é 15

## Adicionando ao seu projeto


**Passo 1.** Adicione-o em seu root build.gradle no final dos repositórios:

    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

**Passo 2.** Adicione a dependência

    dependencies {
	     implementation 'com.github.Flutterwave:rave-android:1.0.42'
	}

**Passo 3.** Adicione a permissão necessária

Adicione as permissões de `READ_PHONE_PERMISSION` e `INTERNET` ao seu android manifest

     <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     <uses-permission android:name="android.permission.INTERNET" />

> **PERMISSÃO NECESSÁRIA**
> Esta biblioteca requer o ** READ_PHONE_PERMISSION ** para obter o número da compilação para detecção e sinalização de fraude, conforme recomendado aqui https://developer.android.com/training/articles/user-data-ids.html#i_abuse_detection_detecting_high_value_stolen_credentials

## Uso

###  1. Crie uma instância de `RavePayManager` 
Defina a chave pública, chave de criptografia e outros parâmetros necessários. O `RavePayManager` aceita uma instância obrigatória da `Activity` de chamada

        new RavePayManager(activity).setAmount(amount)
                        .setCountry(country)
                        .setCurrency(currency)
                        .setEmail(email)
                        .setfName(fName)
                        .setlName(lName)
                        .setNarration(narration)
                        .setPublicKey(publicKey)
                        .setEncryptionKey(encryptionKey)
                        .setTxRef(txRef)
                        .acceptAccountPayments(boolean)
                        .acceptCardPayments(boolean)
                        .acceptMpesaPayments(boolean)
                        .acceptAchPayments(boolean)
                        .acceptGHMobileMoneyPayments(boolean)
                        .acceptUgMobileMoneyPayments(boolean)
                        .acceptBankTransferPayments(boolean)
                        .acceptUssdPayments(boolean)
                        .acceptFrancMobileMoneyPayments(boolean)
                        .onStagingEnv(boolean)
                        .setMeta(List<Meta>)
                        .withTheme(styleId)
                        .isPreAuth(boolean)
                        .setSubAccounts(List<SubAccount>)
                        .shouldDisplayFee(boolean)
                        .showStagingLabel(boolean)
                        .initialize();

| função        | parâmetro           | tipo | requerido  |
| ------------- |:-------------:| -----:| -----:|
| setAmount(amount)      |  Esse é o valor a ser cobrado do cartão/conta | `double` | Requerido
| setCountry(country)     | Este é o país da rota para a transação em relação à moeda. Você pode encontrar uma lista de países e moedas suportados [aqui](https://flutterwavedevelopers.readme.io/docs/multicurrency-payments) | `String` | Requerido
| setCurrency(currency) | Essa é a moeda especificada para cobrar o cartão em | `String` | Requerido
| setfName(fName) | Este é o primeiro nome do titular do cartão ou do cliente  | `String` | Requerido
| setlName(lName) | Este é o sobrenome do titular do cartão ou do cliente | `String` | Requerido
| setEmail(email) | Este é o endereço de email do cliente | `String` | Requerido
| setNarration(narration) | Esta é uma descrição personalizada adicionada pelo comerciante. Para pagamentos por "Transferência bancária", este se torna o nome da conta a ser paga. Veja mais detalhes [aqui](https://developer.flutterwave.com/v2.0/reference#pay-with-bank-transfer-nigeria). | `String` | Não Requerido
| setPublicKey(publicKey) | Chave pública do comerciante. Obtenha aqui suas chaves de comerciante para [ staging](https://flutterwavedevelopers.readme.io/blog/how-to-get-your-staging-keys-from-the-rave-sandbox-environment) and [live](https://flutterwavedevelopers.readme.io/blog/how-to-get-your-live-keys-from-the-rave-dashboard)| `String` | Requerido
| setEncryptionKey(encryptionKey) | Chave de criptografia do comerciante. Obtenha suas chaves de comerciante aqui para [staging](https://flutterwavedevelopers.readme.io/blog/how-to-get-your-staging-keys-from-the-rave-sandbox-environment) e [live](https://flutterwavedevelopers.readme.io/blog/how-to-get-your-live-keys-from-the-rave-dashboard) | `String` | Requerido
| setTxRef(txRef) | Essa é a referência exclusiva, única para a transação específica que está sendo realizada. É gerada pelo comerciante para cada transação | `String` | Requerido
| acceptAccountPayments(boolean) | Defina como `true` se você deseja aceitar pagamentos através de contas bancárias; caso contrário, defina como` false`. | `boolean` | Não Requerido
| acceptCardPayments(boolean) | Defina como `true` se você deseja aceitar pagamentos via cartão; caso contrário, defina como` false` | `boolean` | Não Requerido |
| acceptMpesaPayments(boolean) | Defina como `true` se você quiser aceitar pagamentos Mpesa, caso contrário, defina como` false`. Para que esta opção funcione, você deve definir seu país como `KE` e sua moeda como `KES` | `booleano` | Não Requerido |
| acceptGHMobileMoneyPayments(boolean) | Defina como `true` se você deseja aceitar pagamentos em dinheiro móvel no Gana; caso contrário, defina como `false`. Para que esta opção funcione, você deve definir seu país como `GH` e sua moeda como `GHS`| `boolean` | Não Requerido |
| acceptUgMobileMoneyPayments(boolean) | Defina como `true` se você quiser aceitar pagamentos em dinheiro móvel no Uganda, caso contrário, defina como` false`. Para que esta opção funcione, você deve definir seu país como `UG` e sua moeda como `UGX`| `boolean` | Não Requerido |
| acceptAchPayments(boolean) | Defina como `true` se desejar aceitar cobranças de US ACH de seus clientes; caso contrário, defina como `false`. Para que esta opção funcione, você deve definir seu país como `US` e sua moeda como `USD`. Você também precisa definir `acceptAccountPayments(true)`| `boolean` | Não Requerido |
| acceptBankTransferPayments(boolean) | Defina como `true` se você deseja aceitar pagamentos por transferência bancária de seus clientes; caso contrário, defina como `false`. Atualmente, esta opção está disponível apenas para Naira nigeriana. <br/><br/><strong>Observe:</strong>  Por padrão, os números de conta gerados são dinâmicos. Esse método foi sobrecarregado para obter mais opções, como mostrado abaixo:<br><ul><li>Para gerar contas estáticas (permanentes), passe `true` como um segundo parâmetro. Por exemplo: <br/>```acceptBankTransferPayments(true, true)```</li><li>Para gerar contas que expiram em uma determinada data ou após um certo número de pagamentos, passe valores inteiros para `duration` e `frequency` como tal: <br/>```acceptBankTransferPayments(true, duration, frequency)``` </li></ul>Você pode obter mais detalhes na [documentação da API](https://developer.flutterwave.com/v2.0/reference#pay-with-bank-transfer-nigeria).| `boolean`<br/><br/>Optional overloads:<br/>`boolean`, `boolean`<br/><br/>`boolean`, `int`, `int` | Não Requerido |
| acceptUssdPayments(boolean) | Defina como `true` se você deseja aceitar pagamentos via transferência USSD de seus clientes; caso contrário, defina como `false`. Atualmente, esta opção está disponível apenas para a Naira nigeriana.| `boolean` | Não Requerido |
| acceptFrancMobileMoneyPayments(boolean) | Defina como `true` se você deseja aceitar pagamentos em dinheiro móvel francófono, caso contrário, defina como `false`. Para que esta opção funcione, você deve definir seu país como `NG` e sua moeda como `XOF` para franco CFA da África Ocidental como `Costa do Marfim` OU `XAF` para franco CFA da África Central como `Camarões`. Veja mais detalhes na [documentação da API](https://developer.flutterwave.com/reference#mobile-money-francophone).| `boolean` | Not Required |
| onStagingEnv(boolean) | Defina como `true` se desejar que suas transações sejam executadas no ambiente intermediário, caso contrário, defina como` false`. O padrão é false  | `boolean` | Não Requerido
| setMeta(`List<Meta>`) | Passe outros dados personalizados que você deseja passar. Ele recebe uma `List` de objetos `Meta` | List<Meta> | Não Requerido
| setSubAccounts(`List<SubAccount>`) | Passe uma `List` de `Subconta`, se desejar dividir a taxa de transação com outras pessoas. Subcontas são as contas dos seus fornecedores que você deseja liquidar por transação. Para inicializar uma classe `SubAccount`, faça `SubAccount (String subAccountId, String transactionSplitRatio)` ou `SubAccount (String subAccountId, String transactionSplitRatio, String transactionChargeType, String transactionChargeType, String transactionCharge)` para também cobrar uma taxa pela subconta. [Saiba mais sobre pagamentos divididos e subcontas](https://developer.flutterwave.com/docs/split-payment).| `List<SubAccount>`| Não Requerido
| setIsPreAuth(boolean) | Defina como `true` para pré-autorizar o valor da transação. [Saiba mais sobre pré-autorização](https://developer.flutterwave.com/v2.0/reference#introduction-1). | `int` | Não Requerido
| withTheme(styleId) | Define o tema da interface do usuário. | `int` | Não Requerido
| setPaymentPlan(payment_plan) | Se você deseja fazer pagamentos recorrentes, este é o ID do plano de pagamento a ser usado no pagamento recorrente. Veja como criar planos de pagamento [aqui](https://flutterwavedevelopers.readme.io/v2.0/reference#create-payment-plan) e [aqui](https://flutterwavedevelopers.readme.io/docs/recurring-billing). Isso está disponível apenas para pagamentos com cartão | `String` | Não Requerido
| shouldDisplayFee(boolean) | Defina como `false` para não exibir uma caixa de diálogo para confirmar o valor total (incluindo taxa de cobrança) que a Rave cobrará. Por padrão, isso é definido como `true` | `boolean` | Não Requerido
| showStagingLabel(boolean) | Defina como `false` para não exibir um rótulo de teste no ambiente de teste. Por padrão, isso é definido como `true` | `boolean` | Não Requerido
| initialize() | Inicie a Interface de Pagamento da Rave  |  N/A | Requerido

###  2. Lidar com a resposta
Na atividade de chamada, substitua o método `onActivityResult` para receber a resposta do pagamento, como mostrado abaixo

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
         *  We advise you to do a further verification of transaction's details on your server to be
         *  sure everything checks out before providing service or goods.
        */
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                Toast.makeText(this, "SUCCESS " + message, Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
O objeto `message` da intent contém a resposta JSON bruta da API Rave. Isso pode ser analisado para recuperar qualquer informação de pagamento adicional necessária. Resposta típica de sucesso pode ser encontrada [aqui](https://gist.github.com/bolaware/305ef5a6df7744694d9c35787580a2d2) e a resposta de falha [aqui](https://gist.github.com/bolaware/afa972cbca782bbb942984ddec9f5262).

> **POR FAVOR OBSERVE**
>  Aconselhamos que você faça uma verificação adicional dos detalhes da transação em seu servidor 
>  para garantir que tudo saia antes de fornecer serviços ou mercadorias.

###  3. Customize o visual
Você pode aplicar uma nova aparência alterando a cor de certas partes da interface do usuário para destacar as cores da sua marca

        <style name="DefaultTheme" parent="AppTheme.NoActionBar">
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
        <item name="OTPButtonStyle">@style/otpBtnStyle</item>
        <item name="PayButtonStyle">@style/payBtnStyle</item>
        <item name="PinButtonStyle">@style/pinButtonStyle</item>
        <item name="OTPHeaderStyle">@style/otpHeaderStyle</item>
        <item name="TabLayoutStyle">@style/tabLayoutStyle</item>
        <item name="PinHeaderStyle">@style/pinHeaderStyle</item>
    </style>
## Configurando Proguard
Para configurar o Proguard, adicione as seguintes linhas ao seu arquivo de configuração. Eles manterão os arquivos relacionados a este sdk
```
keepclasseswithmembers public class com.flutterwave.raveandroid.** { *; }
dontwarn com.flutterwave.raveandroid.card.CardFragment
```


##  Ajuda
* Problemas na integração? Participe da nossa [comunidade Slack](https://join.slack.com/t/flutterwavedevelopers/shared_invite/enQtMjU2MjkyNDM5MTcxLWFlOWNlYmE5MTIxNjAwYzc5MDVjZjNhYTJjNTA0ZTQyNDJlMDhhZjJkN2QwZGJmNWMyODhlYjMwNGUyZDQxNTE) for support
* Achou um bug? [Abra uma issue](https://github.com/Flutterwave/rave-android/issues)
* Quer contribuir? [Confira as diretrizes de contribuição]() e [envie um pull request](https://help.github.com/articles/creating-a-pull-request).

## Quer contribuir?

Sinta-se livre para criar problemas e receber solicitações. Quanto mais conciso o pull request, melhor :)

## Licença

```
Rave's Android DropIn UI
MIT License

Copyright (c) 2017

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
