package sh.miles.artisan.parser.node;

import sh.miles.artisan.parser.token.ArtisanParseToken;
import sh.miles.artisan.parser.token.ArtisanTokenizer;
import sh.miles.artisan.test.TestUtil;

import java.util.ArrayList;
import java.util.List;

import static sh.miles.artisan.parser.node.ContainerArtisanSyntaxNode.NodeContainerType.FUNCTION_CONTENT;
import static sh.miles.artisan.parser.node.ContainerArtisanSyntaxNode.NodeContainerType.METADATA;
import static sh.miles.artisan.parser.node.ContainerArtisanSyntaxNode.NodeContainerType.ROOT;
import static sh.miles.artisan.parser.token.ArtisanParseToken.ArtisanTokenType.META;
import static sh.miles.artisan.parser.token.ArtisanParseToken.ArtisanTokenType.OPEN;

public class ArtisanSyntaxNodeTest {

    public static void main(String[] args) {
        final ArtisanSyntaxNode root = ContainerArtisanSyntaxNode.create(ROOT, null);
        ArtisanSyntaxNode currentContainer = ContainerArtisanSyntaxNode.create(METADATA, null);
        currentContainer.addChild(MetadataArtisanSyntaxNode.create(new ArtisanParseToken(0, "key: complex value\t", META)));
        currentContainer.addChild(MetadataArtisanSyntaxNode.create(new ArtisanParseToken(0, "another key: another complex value\t", META)));
        root.addChild(currentContainer);
        currentContainer = ContainerArtisanSyntaxNode.create(FUNCTION_CONTENT, new ArtisanParseToken(0, "AT", OPEN));
        ArtisanSyntaxNode currentSubContainer = new LiteralArtisanSyntaxNode("Some Literal Content");
        currentSubContainer.addChild(new MetadataArtisanSyntaxNode("literlmetakey", "literalmetavalue"));
        currentContainer.addChild(currentSubContainer);
        root.addChild(currentContainer);
        System.out.println(root);

        final List<ArtisanParseToken> tokens = new ArrayList<>();
        final var tokenizer = new ArtisanTokenizer(TestUtil.readResource("parser/token/success-test.ajex"));
        while (tokenizer.hasNext()) {
            tokens.add(tokenizer.next());
        }

        final var ast = ArtisanSyntaxNode.generate(tokens);
        System.out.println(ast);
    }
}
