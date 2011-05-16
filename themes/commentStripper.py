import os, sys
count = errors = 0
def process(f):
    fp = open(f, 'r')
    variation_state = bracket_state = 0
    fw = open(f + '-fixed', 'w')

    numlines = 0
    while True:
        c = fp.read(1)
        
        if not c:
            break
        if c == '\n':
            numlines += 1
            
        if bracket_state == 0:
            if c == '(' or c == '{':
                variation_state += 1
            elif c == ')' or c == '}':
                variation_state -= 1
            elif variation_state <= 0:
                if c == '[':
                    bracket_state += 1

                fw.write(c)
        else:
            if c == ']':
                bracket_state -= 1
            elif c == '[':
                bracket_state += 1
            fw.write(c)
            
        if variation_state < 0:
            print "File has error: %d, %s, %d, %d" % (variation_state, f, fp.tell(), numlines)
            fw.close()
            os.remove(f + '-fixed')
            return False
    return True

for root, dirs, files in os.walk(sys.argv[1]):
    for f in files:
        if f.lower().endswith(".pgn"):
            count += 1
            if count < 590000:
                continue
            f = root + "/" + f
            if os.path.getsize(f) > 10000:
                print "Processing bigass file (%s). %d bytes big" % (f, os.path.getsize(f))
            if not process(f):
                errors += 1
            if count % 1000 == 0: print "%d processed (%f errors)" % (count, float(errors)/count)
         

    
