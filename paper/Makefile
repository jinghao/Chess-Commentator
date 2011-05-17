TARGET=paper.pdf
FIGS=

all: $(TARGET)

$(TARGET): $(TARGET:.pdf=.tex) $(FIGS) *.sty Makefile
	pdflatex $(TEXFLAGS) $<
	bibtex $(basename $<)
	pdflatex $(TEXFLAGS) $<
	pdflatex $(TEXFLAGS) $<

test: test-fonts test-info

test-fonts: $(TARGET)
	pdffonts $<

test-info: $(TARGET)
	pdfinfo $<

print: $(TARGET)
	pdftops $< - | lpr

view: $(TARGET)
	okular $< &

clean:
	rm -f $(TARGET) *.log *.aux *.bbl *.blg *.bak .*.xtx.cache parsetab.py \#*\# *~
