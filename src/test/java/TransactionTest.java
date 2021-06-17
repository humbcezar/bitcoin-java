import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import bitcoinjava.Script;
import bitcoinjava.Transaction;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTest {
    @ParameterizedTest
    @MethodSource("testParameters")
    public void parseVersion(String txHex) throws IOException {
        Transaction transaction = Transaction.fromByteStream(new ByteArrayInputStream(Hex.decode(txHex)));
        assertEquals(BigInteger.ONE, transaction.getVersion());
    }

    @Test
    public void parseInputs() throws IOException {
        String txHex = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        Transaction transaction = Transaction.fromByteStream(new ByteArrayInputStream(Hex.decode(txHex)));
        assertEquals(1, transaction.getInputs().size());
        String expectedPreviousTx = "d1c789a9c60383bf715f3f6ad9d14b91fe55f3deb369fe5d9280cb1a01793f81";
        assertEquals(expectedPreviousTx, transaction.getInputs().get(0).getPreviousTransactionId());
        assertEquals(BigInteger.ZERO, transaction.getInputs().get(0).getPreviousIndex());
        String expectedScriptSig = "6b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278a";
        assertEquals(expectedScriptSig, transaction.getInputs().get(0).getScriptSig().serialize());
        assertEquals(transaction.getInputs().get(0).getSequence(), BigIntegers.fromUnsignedByteArray(Hex.decode("fffffffe")));
    }

    @Test
    public void parseInputsSegwit() throws IOException {
        String txHex = "01000000000101076b57644e155af90f5d9f416b44a3794e0b982c2c427f0845c0e0c62fbb346f0000000000fdffffff0198eb100000000000160014934478b061fa4b5b4dba4f314fb380f3ef77e21902483045022100b7fcf54ae5d7c645b5b44ef7f846e95de9a97a099a447bf8daf14a46f5e3d464022025e709d6794a6fd5b69a7d271fc9a93fcc170b38cfbe5640b6c5d6ec88f021240121025330a1df68c516d32a87ea8ea3da573fa9d86b1b173875beecbf0bdbe45cba8cea7c0a00";
        Transaction transaction = Transaction.fromByteStream(new ByteArrayInputStream(Hex.decode(txHex)));
        assertEquals(1, transaction.getInputs().size());
        String expectedPreviousTx = "6f34bb2fc6e0c045087f422c2c980b4e79a3446b419f5d0ff95a154e64576b07";
        assertEquals(expectedPreviousTx, transaction.getInputs().get(0).getPreviousTransactionId());
        assertEquals(BigInteger.ZERO, transaction.getInputs().get(0).getPreviousIndex());
        String expectedScriptSig = "00";
        assertEquals(expectedScriptSig, transaction.getInputs().get(0).getScriptSig().serialize());
        assertEquals(transaction.getInputs().get(0).getSequence(), BigIntegers.fromUnsignedByteArray(Hex.decode("fffffffd")));
        String expectedWitness = "02483045022100b7fcf54ae5d7c645b5b44ef7f846e95de9a97a099a447bf8daf14a46f5e3d464022025e709d6794a6fd5b69a7d271fc9a93fcc170b38cfbe5640b6c5d6ec88f021240121025330a1df68c516d32a87ea8ea3da573fa9d86b1b173875beecbf0bdbe45cba8c";
        assertEquals(expectedWitness, transaction.getInputs().get(0).getWitness().serialize());
    }

    @Test
    public void parseOutputs() throws IOException {
        String txHex = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        Transaction transaction = Transaction.fromByteStream(new ByteArrayInputStream(Hex.decode(txHex)));
        assertEquals(2, transaction.getOutputs().size());
        BigInteger expectedAmount = BigInteger.valueOf(32454049);
        assertEquals(expectedAmount, transaction.getOutputs().get(0).getAmount());
        String expectedScriptPubkey = "1976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac";
        assertEquals(expectedScriptPubkey, transaction.getOutputs().get(0).getScriptPubkey().serialize());
        BigInteger expectedAmount2 = BigInteger.valueOf(10011545);
        assertEquals(expectedAmount2, transaction.getOutputs().get(1).getAmount());
        String expectedScriptPubkey2 = "1976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac";
        assertEquals(expectedScriptPubkey2, transaction.getOutputs().get(1).getScriptPubkey().serialize());
    }

    @Test
    public void parseOutputsSegwit() throws IOException {
        String txHex = "01000000000101076b57644e155af90f5d9f416b44a3794e0b982c2c427f0845c0e0c62fbb346f0000000000fdffffff0198eb100000000000160014934478b061fa4b5b4dba4f314fb380f3ef77e21902483045022100b7fcf54ae5d7c645b5b44ef7f846e95de9a97a099a447bf8daf14a46f5e3d464022025e709d6794a6fd5b69a7d271fc9a93fcc170b38cfbe5640b6c5d6ec88f021240121025330a1df68c516d32a87ea8ea3da573fa9d86b1b173875beecbf0bdbe45cba8cea7c0a00";
        Transaction transaction = Transaction.fromByteStream(new ByteArrayInputStream(Hex.decode(txHex)));
        assertEquals(1, transaction.getOutputs().size());
        BigInteger expectedAmount = BigInteger.valueOf(1_108_888);
        assertEquals(expectedAmount, transaction.getOutputs().get(0).getAmount());
        String expectedScriptPubkey = "160014934478b061fa4b5b4dba4f314fb380f3ef77e219";
        assertEquals(expectedScriptPubkey, transaction.getOutputs().get(0).getScriptPubkey().serialize());
    }

    @Test
    public void parseLocktime() throws IOException {
        String txHex = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        Transaction transaction = Transaction.fromByteStream(new ByteArrayInputStream(Hex.decode(txHex)));
        BigInteger expectedLocktime = BigInteger.valueOf(410393);
        assertEquals(expectedLocktime, transaction.getLocktime());
    }

    @Test
    public void parseLocktimeSegwit() throws IOException {
        String txHex = "01000000000101076b57644e155af90f5d9f416b44a3794e0b982c2c427f0845c0e0c62fbb346f0000000000fdffffff0198eb100000000000160014934478b061fa4b5b4dba4f314fb380f3ef77e21902483045022100b7fcf54ae5d7c645b5b44ef7f846e95de9a97a099a447bf8daf14a46f5e3d464022025e709d6794a6fd5b69a7d271fc9a93fcc170b38cfbe5640b6c5d6ec88f021240121025330a1df68c516d32a87ea8ea3da573fa9d86b1b173875beecbf0bdbe45cba8cea7c0a00";
        Transaction transaction = Transaction.fromByteStream(new ByteArrayInputStream(Hex.decode(txHex)));
        BigInteger expectedLocktime = BigInteger.valueOf(687338);
        assertEquals(expectedLocktime, transaction.getLocktime());
    }

    @Test
    public void serialize() throws IOException {
        String txHex = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        Transaction transaction = Transaction.fromByteStream(new ByteArrayInputStream(Hex.decode(txHex)));
        assertEquals(txHex, transaction.serialize());
    }

    @Test
    public void serializeSegwit() throws IOException {
        String txHex = "01000000000101076b57644e155af90f5d9f416b44a3794e0b982c2c427f0845c0e0c62fbb346f0000000000fdffffff0198eb100000000000160014934478b061fa4b5b4dba4f314fb380f3ef77e21902483045022100b7fcf54ae5d7c645b5b44ef7f846e95de9a97a099a447bf8daf14a46f5e3d464022025e709d6794a6fd5b69a7d271fc9a93fcc170b38cfbe5640b6c5d6ec88f021240121025330a1df68c516d32a87ea8ea3da573fa9d86b1b173875beecbf0bdbe45cba8cea7c0a00";
        Transaction transaction = Transaction.fromByteStream(new ByteArrayInputStream(Hex.decode(txHex)));
        assertEquals(txHex, transaction.serialize());
    }

    @Test
    public void sigHash() throws IOException, NoSuchAlgorithmException {
        String txHex = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        String scriptPubkeyHex = "1976a914a802fc56c704ce87c42d7c92eb75e7896bdc41ae88ac";
        Transaction transaction = Transaction.fromByteStream(new ByteArrayInputStream(Hex.decode(txHex)));
        String expectedSighash = "27e0c5994dec7824e56dec6b2fcb342eb7cdb0d0957c2fce9882f715e85d81a6";
        assertEquals(expectedSighash, transaction.sigHash(0, Script.fromByteStream(new ByteArrayInputStream(Hex.decode(scriptPubkeyHex)))));
    }

    @Test
    public void sigHashSegwit() throws IOException, NoSuchAlgorithmException {
        String txHex = "0100000002fff7f7881a8099afa6940d42d1e7f6362bec38171ea3edf433541db4e4ad969f0000000000eeffffffef51e1b804cc89d182d279655c3aa89e815b1b309fe287d9b2b55d57b90ec68a0100000000ffffffff02202cb206000000001976a9148280b37df378db99f66f85c95a783a76ac7a6d5988ac9093510d000000001976a9143bde42dbee7e4dbe6a21b2d50ce2f0167faa815988ac11000000";
        String hash160Pubkey = "141d0f172a0ecb48aee1be1f2687d2963ae33f71a1";
        BigInteger amount = BigInteger.valueOf(600_000_000);
        Transaction transaction = Transaction.fromByteStream(new ByteArrayInputStream(Hex.decode(txHex)));
        String expectedSighash = "c37af31116d1b27caf68aae9e3ac82f1477929014d5b917657d0eb49478cb670";
        assertEquals(expectedSighash, transaction.sigHashSegwit(1, Script.p2pkhScript(hash160Pubkey), amount));
    }

    private static Stream<Arguments> testParameters() {
        return Stream.of(
            Arguments.of(
                "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600"
            ),
            Arguments.of(
                "01000000000101076b57644e155af90f5d9f416b44a3794e0b982c2c427f0845c0e0c62fbb346f0000000000fdffffff0198eb100000000000160014934478b061fa4b5b4dba4f314fb380f3ef77e21902483045022100b7fcf54ae5d7c645b5b44ef7f846e95de9a97a099a447bf8daf14a46f5e3d464022025e709d6794a6fd5b69a7d271fc9a93fcc170b38cfbe5640b6c5d6ec88f021240121025330a1df68c516d32a87ea8ea3da573fa9d86b1b173875beecbf0bdbe45cba8cea7c0a00"
            )
        );
    }

}
