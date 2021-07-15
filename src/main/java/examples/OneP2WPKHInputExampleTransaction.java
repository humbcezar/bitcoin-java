package examples;

import bitcoinjava.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.math.BigInteger;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import static bitcoinjava.AddressConstants.*;

public class OneP2WPKHInputExampleTransaction {
    public static void main(String[] args) throws IOException {
        Security.addProvider(new BouncyCastleProvider());

        String secret = "4b357284216a4262a36cc166018b9302";
        System.out.println("private key for p2wpkh input: " + secret);
        PrivateKey privateKey = new PrivateKey(new BigInteger(1, Hex.decode(secret)));
        System.out.println("address for p2wpkh input: " + privateKey.getPublicKey().segwitAddressFromCompressedPublicKey(TESTNET_P2WPKH_ADDRESS_PREFIX));

        String p2wpkhInputTransactionId = "67e41a52c17499cee80aa9e62f9c6e68c5c5432bd7ecedbf62c308fd28d79113";
        TransactionInput transactionInput1 = new TransactionInput(
            p2wpkhInputTransactionId,
            BigInteger.ONE,
            new Script(List.of()),
            new BigInteger(1, Hex.decode("ffffffff"))
        );
        ArrayList<TransactionInput> transactionInputArrayList = new ArrayList<>();
        transactionInputArrayList.add(transactionInput1);

        BigInteger amount = BigInteger.valueOf(9_000);
//        TransactionOutput transactionOutputChange = new TransactionOutput(amount2, Script.p2wpkhScript(Bech32.decode("tb", "tb1q63rv8027mnhszkmf0f5qkxhk48r9tcyk0n6m8l")[1]));
        Script script = Script.p2trScript(Bech32.decode("tb", "tb1psmxksw0jx8eu5ds5yphsszyjagw5ug2ce2z35j0mk8ytkunh3f2sugn56k")[1]);
        TransactionOutput transactionOutputChange = new TransactionOutput(amount, script);
        System.out.println("output 0 address: " + "tb1psmxksw0jx8eu5ds5yphsszyjagw5ug2ce2z35j0mk8ytkunh3f2sugn56k");
        System.out.println("output 0 amount: " + "10,000 satoshis");

        ArrayList<TransactionOutput> transactionOutputArrayList = new ArrayList<>();
        transactionOutputArrayList.add(transactionOutputChange);

        Transaction transaction = new Transaction(BigInteger.ONE, transactionInputArrayList, transactionOutputArrayList, BigInteger.ZERO, true);
        System.out.println("unsigned transaction: " + transaction.serialize());
        TransactionECDSASigner.sign(transaction, privateKey, 0, BigInteger.valueOf(10_000), true);

        System.out.println("signed transaction: " + transaction.serialize());
    }
}
