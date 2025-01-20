package sh.miles.artisan.visitor;

import sh.miles.artisan.parser.node.ArtisanSyntaxNode;
import sh.miles.artisan.parser.node.ContainerArtisanSyntaxNode;
import sh.miles.artisan.parser.node.LiteralArtisanSyntaxNode;
import sh.miles.artisan.parser.node.MetadataArtisanSyntaxNode;

import java.io.PrintStream;

public class PrinterNodeVisitor implements ArtisanNodeVisitor {

    private final PrintStream stream;

    public PrinterNodeVisitor(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public void startVisit(final ContainerArtisanSyntaxNode root) {
        stream.println("Start Visit");
    }

    @Override
    public void visitContainer(final ContainerArtisanSyntaxNode parent, final ContainerArtisanSyntaxNode container) {
        stream.println(container.containerType + " " + container.name);
    }

    @Override
    public void visitLiteral(final ContainerArtisanSyntaxNode parent, final LiteralArtisanSyntaxNode literal) {
        stream.println(literal.literal);
    }

    @Override
    public void visitMeta(final ArtisanSyntaxNode parent, final MetadataArtisanSyntaxNode meta) {
        System.out.println(meta.key + ", " + meta.value);
    }

    @Override
    public void endVisit(final ContainerArtisanSyntaxNode root) {
        stream.println("End Visit");
    }
}
