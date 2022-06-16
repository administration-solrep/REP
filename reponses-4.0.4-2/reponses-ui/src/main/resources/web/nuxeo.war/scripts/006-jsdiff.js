/*JavaScript diff algorithm by [[User:Cacycle]] (http://en.wikipedia.org/wiki/User_talk:Cacycle).
Outputs html/css-formatted new text with highlighted deletions, inserts, and block moves.

The program uses cross-browser code and should work with all modern browsers. It has been tested with:
* Mozilla Firefox 1.5.0.1
* Mozilla SeaMonkey 1.0
* Opera 8.53
* Internet Explorer 6.0.2900.2180

The code is currently under active development and might change rapidly.

An implementation of:
Paul Heckel: A technique for isolating differences between files
Communications of the ACM 21(4):264 (1978)
http://doi.acm.org/10.1145/359460.359467

* Word types have been optimized for Wikipedia source texts
* Additional post-pass 5 code for resolving islands caused by adding
	two common words at the end of sequences of common words
* Additional detection of block borders and color coding of moved blocks and their original position

This code is used by [[User:Cacycle/editor.js]] ([[User:Cacycle/editor]]).

Usage: var htmlText = stringDiff(oldText, newText);

This code has been released into the public domain.

Datastructures:

text: an object that holds all text related datastructures
	.newWords: consecutive words of the new text (N)
	.oldWords: consecutive words of the old text (O)
	.newToOld: array of corresponding word number in old text (NA)
	.oldToNew: array of corresponding word number in new text (OA)
	.message:  output message for testing purposes

symbol['word']: symbol table for passes 1 - 3, holds words as a hash
	.newCtr:  new word occurences counter (NC)
	.oldCtr:  old word occurences counter (OC)
	.toNew:   table last old word number
	.toOld:   last new word number (OLNA)

block: an object that holds block move information
	blocks indexed after new text:
	.newStart:  new text word number of start of this block
	.newLength: element number of this block including non-words
	.newWords:  true word number of this block
	.newNumber: corresponding block index in old text
	.newBlock:  moved-block-number of a block that has been moved here
	.newLeft:   moved-block-number of a block that has been moved from this border leftwards
	.newRight:  moved-block-number of a block that has been moved from this border rightwards
	.newLeftIndex:  index number of a block that has been moved from this border leftwards
	.newRightIndex: index number of a block that has been moved from this border rightwards
	blocks indexed after old text:
	.oldStart:  word number of start of this block
	.oldToNew:  corresponding new text word number of start
	.oldLength: element number of this block including non-words
	.oldWords:  true word number of this block

*/

// css for change indicators
var styleDelete = styleDelete || "versioncomparison__del";
var styleInsert = styleInsert || "versioncomparison__ins";
var styleMoved =
    styleMoved || "font-weight: bold; vertical-align: text-bottom; font-size: xx-small; padding: 0; border: solid 1px;";
var styleBlock = styleBlock || [
    "background-color: #ffff44;",
    "background-color: #b0ff90;",
    "background-color: #ffcc99;",
    "background-color: #99ffff;",
    "background-color: #99ccff;",
    "background-color: #cc99ff;",
    "background-color: #ff99cc;",
    "background-color: #ffd040;",
    "background-color: #d0d0d0;",
];

// html for change indicators, {number} is replaced by the block number, {block} is replaced by the block style
var htmlMovedRight = htmlMovedRight || '<input type="button" value="&gt;" style="' + styleMoved + ' {block}"/>';
var htmlMovedLeft = htmlMovedLeft || '<input type="button" value="&lt;" style="' + styleMoved + ' {block}"/>';

var htmlBlockStart = htmlBlockStart || '<span style="{block}">';
var htmlBlockEnd = htmlBlockEnd || "</span>";

var htmlDeleteStart =
    htmlDeleteStart || '<del class="' + styleDelete + '">[ <span class="sr-only">Début suppression</span>';
var htmlDeleteEnd = htmlDeleteEnd || '<span class="sr-only">Fin suppression</span> ]</del>';

var htmlInsertStart = htmlInsertStart || '<ins class="' + styleInsert + '">[ <span class="sr-only">Début ajout</span>';
var htmlInsertEnd = htmlInsertEnd || '<span class="sr-only">Fin ajout</span> ]</ins>';

// minimal number of real words for a moved block (0 for always displaying block move indicators)
var blockMinLength = blockMinLength || 1000000;

// exclude identical sequence starts and endings from change marking
var wordDiff = wordDiff || true;

// enable recursive diff to resolve problematic sequences
var recursiveDiff = recursiveDiff || true;

// enable block move display
var showBlockMoves = showBlockMoves || true;

// global variables
var i;
var j;
var m;
var n;

// StringDiff: main program
// input: oldText, newText, strings containing the texts
// returns: html diff

function StringDiff(oldText, newText) {
    var text = {};
    text.newWords = [];
    text.oldWords = [];
    text.newToOld = [];
    text.oldToNew = [];
    text.message = "";
    var block = {};
    var outText = "";

    // trap trivial changes: no change
    if (oldText == newText) {
        outText = newText;
        outText = Escape(outText);
        outText = HtmlFormat(outText);
        return outText;
    }

    // trap trivial changes: old text deleted
    if (oldText == null || oldText.length == 0) {
        outText = newText;
        outText = Escape(outText);
        outText = HtmlFormat(outText);
        outText = htmlInsertStart + outText + htmlInsertEnd;
        return outText;
    }

    // trap trivial changes: new text deleted
    if (newText == null || newText.length == 0) {
        outText = oldText;
        outText = Escape(outText);
        outText = HtmlFormat(outText);
        outText = htmlDeleteStart + outText + htmlDeleteEnd;
        return outText;
    }

    // split new and old text into words
    SplitText(oldText, newText, text);

    // calculate diff information
    TextDiff(text);

    //detect block borders and moved blocks
    DetectBlocks(text, block);

    // process diff data into formatted html text
    outText = DiffToHtml(text, block);

    return outText;
}

function StringDiff1(oldText, newText) {
    var text = {};
    text.newWords = [];
    text.oldWords = [];
    text.newToOld = [];
    text.oldToNew = [];
    text.message = "";
    var block = {};
    var outText = "";

    // trap trivial changes: no change
    if (oldText == newText) {
        outText = newText;
        outText = Escape(outText);
        outText = HtmlFormat(outText);
        return outText;
    }

    // trap trivial changes: old text deleted
    if (oldText == null || oldText.length == 0) {
        outText = newText;
        outText = Escape(outText);
        outText = HtmlFormat(outText);
        outText = htmlInsertStart + outText + htmlInsertEnd;
        return outText;
    }

    // trap trivial changes: new text deleted
    /*if ( (newText == null) || (newText.length == 0) ) {
		outText = oldText;
		outText = Escape(outText);
		outText = HtmlFormat(outText);
		outText = htmlDeleteStart + outText + htmlDeleteEnd;
		return(outText);
	}*/

    // split new and old text into words
    SplitText(oldText, newText, text);

    // calculate diff information
    TextDiff(text);

    //detect block borders and moved blocks
    DetectBlocks(text, block);

    // process diff data into formatted html text
    outText = DiffToHtml1(text, block);

    return outText;
}

function StringDiff2(oldText, newText) {
    var text = {};
    text.newWords = [];
    text.oldWords = [];
    text.newToOld = [];
    text.oldToNew = [];
    text.message = "";
    var block = {};
    var outText = "";

    // trap trivial changes: no change
    if (oldText == newText) {
        outText = newText;
        outText = Escape(outText);
        outText = HtmlFormat(outText);
        return outText;
    }

    // trap trivial changes: old text deleted
    /*if ( (oldText == null) || (oldText.length == 0) ) {
		outText = newText;
		outText = Escape(outText);
		outText = HtmlFormat(outText);
		outText = htmlInsertStart + outText + htmlInsertEnd;
		return(outText);
	}*/

    // trap trivial changes: new text deleted
    if (newText == null || newText.length == 0) {
        outText = oldText;
        outText = Escape(outText);
        outText = HtmlFormat(outText);
        outText = htmlDeleteStart + outText + htmlDeleteEnd;
        return outText;
    }

    // split new and old text into words
    SplitText(oldText, newText, text);

    // calculate diff information
    TextDiff(text);

    //detect block borders and moved blocks
    DetectBlocks(text, block);

    // process diff data into formatted html text
    outText = DiffToHtml2(text, block);

    return outText;
}

// SplitText: split new and old text into words
// input: oldText, newText, strings containing the texts
// changes: text.newWords and text.oldWords, arrays containing the texts in arrays of words

function SplitText(oldText, newText, text) {
    // convert strange spaces
    oldText = oldText.replace(/[\t\v\u00A0\u2028\u2029]+/g, " ");
    newText = newText.replace(/[\t\v\u00A0\u2028\u2029]+/g, " ");

    // split old text into words

    //              /     |    |    |    |    |   |  |     |   |  |  |    |    |    | /
    var pattern = /[\w]+|\[\[|\]\]|\{\{|\}\}|\n+| +|&\w+;|'''|''|=+|\{\||\|\}|\|\-|./g;
    var result;
    do {
        result = pattern.exec(oldText);
        if (result != null) {
            text.oldWords.push(result[0]);
        }
    } while (result != null);

    // split new text into words
    do {
        result = pattern.exec(newText);
        if (result != null) {
            text.newWords.push(result[0]);
        }
    } while (result != null);

    return;
}

// TextDiff: calculate diff information
// input: text.newWords and text.oldWords, arrays containing the texts in arrays of words
// optionally for recursive calls: newStart, newEnd, oldStart, oldEnd, recursionLevel
// changes: text.newToOld and text.oldToNew, containing the line numbers in the other version

function TextDiff(text, newStart, newEnd, oldStart, oldEnd, recursionLevel) {
    symbol = new Object();
    symbol.newCtr = [];
    symbol.oldCtr = [];
    symbol.toNew = [];
    symbol.toOld = [];

    // set defaults
    newStart = newStart || 0;
    newEnd = newEnd || text.newWords.length;
    oldStart = oldStart || 0;
    oldEnd = oldEnd || text.oldWords.length;
    recursionLevel = recursionLevel || 0;

    // limit recursion depth
    if (recursionLevel > 10) {
        return;
    }

    // pass 1: parse new text into symbol table s

    var word;
    for (i = newStart; i < newEnd; i++) {
        word = text.newWords[i];

        // add new entry to symbol table
        if (symbol[word] == null) {
            symbol[word] = { newCtr: 0, oldCtr: 0, toNew: null, toOld: null };
        }

        // increment symbol table word counter for new text
        symbol[word].newCtr++;

        // add last word number in new text
        symbol[word].toNew = i;
    }

    // pass 2: parse old text into symbol table

    for (j = oldStart; j < oldEnd; j++) {
        word = text.oldWords[j];

        // add new entry to symbol table
        if (symbol[word] == null) {
            symbol[word] = { newCtr: 0, oldCtr: 0, toNew: null, toOld: null };
        }

        // increment symbol table word counter for old text
        symbol[word].oldCtr++;

        // add last word number in old text
        symbol[word].toOld = j;
    }

    // pass 3: connect unique words

    for (i in symbol) {
        // find words in the symbol table that occur only once in both versions
        if (symbol[i].newCtr == 1 && symbol[i].oldCtr == 1) {
            var toNew = symbol[i].toNew;
            var toOld = symbol[i].toOld;

            // do not use spaces as unique markers
            if (!/\s/.test(text.newWords[toNew])) {
                // connect from new to old and from old to new
                text.newToOld[toNew] = toOld;
                text.oldToNew[toOld] = toNew;
            }
        }
    }

    // pass 4: connect adjacent identical words downwards

    for (i = newStart; i < newEnd - 1; i++) {
        // find already connected pairs
        if (text.newToOld[i] != null) {
            j = text.newToOld[i];

            // check if the following words are not yet connected
            if (text.newToOld[i + 1] == null && text.oldToNew[j + 1] == null) {
                // if the following words are the same connect them
                if (text.newWords[i + 1] == text.oldWords[j + 1]) {
                    text.newToOld[i + 1] = j + 1;
                    text.oldToNew[j + 1] = i + 1;
                }
            }
        }
    }

    // pass 5: connect adjacent identical words upwards

    for (i = newEnd - 1; i > newStart; i--) {
        // find already connected pairs
        if (text.newToOld[i] != null) {
            j = text.newToOld[i];

            // check if the preceeding words are not yet connected
            if (text.newToOld[i - 1] == null && text.oldToNew[j - 1] == null) {
                // if the preceeding words are the same connect them
                if (text.newWords[i - 1] == text.oldWords[j - 1]) {
                    text.newToOld[i - 1] = j - 1;
                    text.oldToNew[j - 1] = i - 1;
                }
            }
        }
    }

    // recursively diff still unresolved regions downwards

    if (recursiveDiff) {
        i = newStart;
        j = oldStart;
        while (i < newEnd) {
            if (text.newToOld[i - 1] != null) {
                j = text.newToOld[i - 1] + 1;
            }

            // check for the start of an unresolved sequence
            if (text.newToOld[i] == null && text.oldToNew[j] == null) {
                // determine the ends of the sequences
                var iStart = i;
                var iEnd = i;
                while (text.newToOld[iEnd] == null && iEnd < newEnd) {
                    iEnd++;
                }
                var iLength = iEnd - iStart;

                var jStart = j;
                var jEnd = j;
                while (text.oldToNew[jEnd] == null && jEnd < oldEnd) {
                    jEnd++;
                }
                var jLength = jEnd - jStart;

                // recursively diff the unresolved sequence
                if (iLength > 0 && jLength > 0) {
                    if (iLength > 1 || jLength > 1) {
                        if (iStart != newStart || iEnd != newEnd || jStart != oldStart || jEnd != oldEnd) {
                            TextDiff(text, iStart, iEnd, jStart, jEnd, recursionLevel + 1);
                        }
                    }
                }
                i = iEnd;
            } else {
                i++;
            }
        }
    }

    // recursively diff still unresolved regions upwards

    if (recursiveDiff) {
        i = newEnd - 1;
        j = oldEnd - 1;
        while (i >= newStart) {
            if (text.newToOld[i + 1] != null) {
                j = text.newToOld[i + 1] - 1;
            }

            // check for the start of an unresolved sequence
            if (text.newToOld[i] == null && text.oldToNew[j] == null) {
                // determine the ends of the sequences
                var iStart = i;
                var iEnd = i + 1;
                while (text.newToOld[iStart - 1] == null && iStart >= newStart) {
                    iStart--;
                }
                var iLength = iEnd - iStart;

                var jStart = j;
                var jEnd = j + 1;
                while (text.oldToNew[jStart - 1] == null && jStart >= oldStart) {
                    jStart--;
                }
                var jLength = jEnd - jStart;

                // recursively diff the unresolved sequence
                if (iLength > 0 && jLength > 0) {
                    if (iLength > 1 || jLength > 1) {
                        if (iStart != newStart || iEnd != newEnd || jStart != oldStart || jEnd != oldEnd) {
                            TextDiff(text, iStart, iEnd, jStart, jEnd, recursionLevel + 1);
                        }
                    }
                }
                i = iStart - 1;
            } else {
                i--;
            }
        }
    }
    return;
}

// DiffToHtml: process diff data into formatted html text
// input: text.newWords and text.oldWords, arrays containing the texts in arrays of words
//   text.newToOld and text.oldToNew, containing the line numbers in the other version
//   block data structure
// returns: outText, a html string

function DiffToHtml(text, block) {
    var outText = text.message;

    var blockNumber = 0;
    i = 0;
    j = 0;
    do {
        var movedIndex = [];
        var movedBlock = [];
        var movedLeft = [];
        var blockText = "";
        var identText = "";
        var delText = "";
        var insText = "";
        var identStart = "";
        var identEnd = "";

        // detect block boundary
        if (text.newToOld[i] != j || blockNumber == 0) {
            if (
                (text.newToOld[i] != null || i >= text.newWords.length) &&
                (text.oldToNew[j] != null || j >= text.oldWords.length)
            ) {
                // block moved right
                var moved = block.newRight[blockNumber];
                if (moved > 0) {
                    var index = block.newRightIndex[blockNumber];
                    movedIndex.push(index);
                    movedBlock.push(moved);
                    movedLeft.push(false);
                }

                // block moved left
                moved = block.newLeft[blockNumber];
                if (moved > 0) {
                    var index = block.newLeftIndex[blockNumber];
                    movedIndex.push(index);
                    movedBlock.push(moved);
                    movedLeft.push(true);
                }

                // check if a block starts here
                moved = block.newBlock[blockNumber];
                if (moved > 0) {
                    // mark block as inserted text
                    if (block.newWords[blockNumber] < blockMinLength) {
                        identStart = htmlInsertStart;
                        identEnd = htmlInsertEnd;
                    }

                    // mark block by color
                    else {
                        if (moved > styleBlock.length) {
                            moved = styleBlock.length;
                        }
                        identStart = HtmlCustomize(htmlBlockStart, moved - 1);
                        identEnd = htmlBlockEnd;
                    }
                }

                if (i >= text.newWords.length) {
                    i++;
                } else {
                    j = text.newToOld[i];
                    blockNumber++;
                }
            }
        }

        // get the correct order if moved to the left as well as to the right from here
        if (movedIndex.length == 2) {
            if (movedIndex[0] > movedIndex[1]) {
                movedIndex.reverse();
                movedBlock.reverse();
                movedLeft.reverse();
            }
        }

        // handle left and right block moves from this position
        for (m = 0; m < movedIndex.length; m++) {
            // insert the block as deleted text
            if (block.newWords[movedIndex[m]] < blockMinLength) {
                var movedStart = block.newStart[movedIndex[m]];
                var movedLength = block.newLength[movedIndex[m]];
                var str = "";
                for (n = movedStart; n < movedStart + movedLength; n++) {
                    str += text.newWords[n];
                }
                str = str.replace(/\n/g, "&para;<br/>");
                blockText += htmlDeleteStart + Escape(str) + htmlDeleteEnd;
                blockText += Escape(str);
            }

            // add a placeholder / move direction indicator
            else {
                if (movedBlock[m] > styleBlock.length) {
                    movedBlock[m] = styleBlock.length;
                }
                if (movedLeft[m]) {
                    blockText += HtmlCustomize(htmlMovedLeft, movedBlock[m] - 1);
                } else {
                    blockText += HtmlCustomize(htmlMovedRight, movedBlock[m] - 1);
                }
            }
        }

        // collect consecutive identical text
        while (i < text.newWords.length && j < text.oldWords.length) {
            if (text.newToOld[i] == null || text.oldToNew[j] == null) {
                break;
            }
            if (text.newToOld[i] != j) {
                break;
            }
            identText += text.newWords[i];
            i++;
            j++;
        }

        // collect consecutive deletions
        while (text.oldToNew[j] == null && j < text.oldWords.length) {
            delText += text.oldWords[j];
            j++;
        }

        // collect consecutive inserts
        while (text.newToOld[i] == null && i < text.newWords.length) {
            insText += text.newWords[i];
            i++;
        }

        // remove leading and trailing similarities betweein delText and ins from highlighting
        var preText = "";
        var postText = "";
        if (wordDiff) {
            if (delText != "" && insText != "") {
                // remove leading similarities
                while (delText.charAt(0) == insText.charAt(0)) {
                    preText = preText + delText.charAt(0);
                    delText = delText.substr(1);
                    insText = insText.substr(1);
                }

                // remove trailing similarities
                while (delText.charAt(delText.length - 1) == insText.charAt(insText.length - 1)) {
                    postText = delText.charAt(delText.length - 1) + postText;
                    delText = delText.substr(0, delText.length - 1);
                    insText = insText.substr(0, insText.length - 1);
                }
            }
        }

        // output the identical text, deletions and inserts
        if (blockText != "") {
            outText += blockText;
        }
        if (identText != "") {
            outText += identStart + Escape(identText) + identEnd;
        }
        outText += preText;
        if (delText != "") {
            delText = htmlDeleteStart + Escape(delText) + htmlDeleteEnd;
            delText = delText.replace(/\n/g, "&para;<br/>");
            outText += delText;
        }
        if (insText != "") {
            insText = htmlInsertStart + Escape(insText) + htmlInsertEnd;
            insText = insText.replace(/\n/g, "&para;<br/>");
            outText += insText;
        }
        outText += postText;
    } while (i <= text.newWords.length);

    outText += "\n";
    outText = HtmlFormat(outText);

    return outText;
}

function DiffToHtml1(text, block) {
    var outText = text.message;

    var blockNumber = 0;
    i = 0;
    j = 0;
    do {
        var movedIndex = [];
        var movedBlock = [];
        var movedLeft = [];
        var blockText = "";
        var identText = "";
        var delText = "";
        var insText = "";
        var identStart = "";
        var identEnd = "";

        // detect block boundary
        if (text.newToOld[i] != j || blockNumber == 0) {
            if (
                (text.newToOld[i] != null || i >= text.newWords.length) &&
                (text.oldToNew[j] != null || j >= text.oldWords.length)
            ) {
                // block moved right
                var moved = block.newRight[blockNumber];
                if (moved > 0) {
                    var index = block.newRightIndex[blockNumber];
                    movedIndex.push(index);
                    movedBlock.push(moved);
                    movedLeft.push(false);
                }

                // block moved left
                moved = block.newLeft[blockNumber];
                if (moved > 0) {
                    var index = block.newLeftIndex[blockNumber];
                    movedIndex.push(index);
                    movedBlock.push(moved);
                    movedLeft.push(true);
                }

                // check if a block starts here
                moved = block.newBlock[blockNumber];
                if (moved > 0) {
                    // mark block as inserted text
                    if (block.newWords[blockNumber] < blockMinLength) {
                        identStart = htmlInsertStart;
                        identEnd = htmlInsertEnd;
                    }

                    // mark block by color
                    else {
                        if (moved > styleBlock.length) {
                            moved = styleBlock.length;
                        }
                        identStart = HtmlCustomize(htmlBlockStart, moved - 1);
                        identEnd = htmlBlockEnd;
                    }
                }

                if (i >= text.newWords.length) {
                    i++;
                } else {
                    j = text.newToOld[i];
                    blockNumber++;
                }
            }
        }

        // get the correct order if moved to the left as well as to the right from here
        if (movedIndex.length == 2) {
            if (movedIndex[0] > movedIndex[1]) {
                movedIndex.reverse();
                movedBlock.reverse();
                movedLeft.reverse();
            }
        }

        // handle left and right block moves from this position
        for (m = 0; m < movedIndex.length; m++) {
            // insert the block as deleted text
            if (block.newWords[movedIndex[m]] < blockMinLength) {
                var movedStart = block.newStart[movedIndex[m]];
                var movedLength = block.newLength[movedIndex[m]];
                var str = "";
                for (n = movedStart; n < movedStart + movedLength; n++) {
                    str += text.newWords[n];
                }
                str = str.replace(/\n/g, "&para;<br/>");
                //blockText += htmlDeleteStart + Escape(str) + htmlDeleteEnd;
                blockText += Escape(str);
            }

            // add a placeholder / move direction indicator
            else {
                if (movedBlock[m] > styleBlock.length) {
                    movedBlock[m] = styleBlock.length;
                }
                if (movedLeft[m]) {
                    blockText += HtmlCustomize(htmlMovedLeft, movedBlock[m] - 1);
                } else {
                    blockText += HtmlCustomize(htmlMovedRight, movedBlock[m] - 1);
                }
            }
        }

        // collect consecutive identical text
        while (i < text.newWords.length && j < text.oldWords.length) {
            if (text.newToOld[i] == null || text.oldToNew[j] == null) {
                break;
            }
            if (text.newToOld[i] != j) {
                break;
            }
            identText += text.newWords[i];
            i++;
            j++;
        }

        // collect consecutive deletions
        while (text.oldToNew[j] == null && j < text.oldWords.length) {
            delText += text.oldWords[j];
            j++;
        }

        // collect consecutive inserts
        while (text.newToOld[i] == null && i < text.newWords.length) {
            insText += text.newWords[i];
            i++;
        }

        // remove leading and trailing similarities betweein delText and ins from highlighting
        var preText = "";
        var postText = "";
        if (wordDiff) {
            if (delText != "" && insText != "") {
                // remove leading similarities
                while (delText.charAt(0) == insText.charAt(0)) {
                    preText = preText + delText.charAt(0);
                    delText = delText.substr(1);
                    insText = insText.substr(1);
                }

                // remove trailing similarities
                while (delText.charAt(delText.length - 1) == insText.charAt(insText.length - 1)) {
                    postText = delText.charAt(delText.length - 1) + postText;
                    delText = delText.substr(0, delText.length - 1);
                    insText = insText.substr(0, insText.length - 1);
                }
            }
        }

        // output the identical text, deletions and inserts
        if (blockText != "") {
            outText += blockText;
        }
        if (identText != "") {
            outText += identStart + Escape(identText) + identEnd;
        }
        outText += preText;
        /*if (delText != '') {
		delText = htmlDeleteStart + Escape(delText) + htmlDeleteEnd;
		delText = delText.replace(/\n/g, '&para;<br/>');
		outText += delText;
	}*/
        if (insText != "") {
            insText = htmlInsertStart + Escape(insText) + htmlInsertEnd;
            insText = insText.replace(/\n/g, "&para;<br/>");
            outText += insText;
        }
        outText += postText;
    } while (i <= text.newWords.length);

    outText += "\n";
    outText = HtmlFormat(outText);

    return outText;
}

function DiffToHtml2(text, block) {
    var outText = text.message;

    var blockNumber = 0;
    i = 0;
    j = 0;
    do {
        var movedIndex = [];
        var movedBlock = [];
        var movedLeft = [];
        var blockText = "";
        var identText = "";
        var delText = "";
        var insText = "";
        var identStart = "";
        var identEnd = "";

        // detect block boundary
        if (text.newToOld[i] != j || blockNumber == 0) {
            if (
                (text.newToOld[i] != null || i >= text.newWords.length) &&
                (text.oldToNew[j] != null || j >= text.oldWords.length)
            ) {
                // block moved right
                var moved = block.newRight[blockNumber];
                if (moved > 0) {
                    var index = block.newRightIndex[blockNumber];
                    movedIndex.push(index);
                    movedBlock.push(moved);
                    movedLeft.push(false);
                }

                // block moved left
                moved = block.newLeft[blockNumber];
                if (moved > 0) {
                    var index = block.newLeftIndex[blockNumber];
                    movedIndex.push(index);
                    movedBlock.push(moved);
                    movedLeft.push(true);
                }

                // check if a block starts here
                moved = block.newBlock[blockNumber];
                if (moved > 0) {
                    // mark block as inserted text
                    if (block.newWords[blockNumber] < blockMinLength) {
                        /*identStart = htmlInsertStart;
					identEnd = htmlInsertEnd;*/
                    }

                    // mark block by color
                    else {
                        if (moved > styleBlock.length) {
                            moved = styleBlock.length;
                        }
                        identStart = HtmlCustomize(htmlBlockStart, moved - 1);
                        identEnd = htmlBlockEnd;
                    }
                }

                if (i >= text.newWords.length) {
                    i++;
                } else {
                    j = text.newToOld[i];
                    blockNumber++;
                }
            }
        }

        // get the correct order if moved to the left as well as to the right from here
        if (movedIndex.length == 2) {
            if (movedIndex[0] > movedIndex[1]) {
                movedIndex.reverse();
                movedBlock.reverse();
                movedLeft.reverse();
            }
        }

        // handle left and right block moves from this position
        for (m = 0; m < movedIndex.length; m++) {
            // insert the block as deleted text
            if (block.newWords[movedIndex[m]] < blockMinLength) {
                var movedStart = block.newStart[movedIndex[m]];
                var movedLength = block.newLength[movedIndex[m]];
                var str = "";
                for (n = movedStart; n < movedStart + movedLength; n++) {
                    str += text.newWords[n];
                }
                str = str.replace(/\n/g, "&para;<br/>");
                blockText += htmlDeleteStart + Escape(str) + htmlDeleteEnd;
            }

            // add a placeholder / move direction indicator
            else {
                if (movedBlock[m] > styleBlock.length) {
                    movedBlock[m] = styleBlock.length;
                }
                if (movedLeft[m]) {
                    blockText += HtmlCustomize(htmlMovedLeft, movedBlock[m] - 1);
                } else {
                    blockText += HtmlCustomize(htmlMovedRight, movedBlock[m] - 1);
                }
            }
        }

        // collect consecutive identical text
        while (i < text.newWords.length && j < text.oldWords.length) {
            if (text.newToOld[i] == null || text.oldToNew[j] == null) {
                break;
            }
            if (text.newToOld[i] != j) {
                break;
            }
            identText += text.newWords[i];
            i++;
            j++;
        }

        // collect consecutive deletions
        while (text.oldToNew[j] == null && j < text.oldWords.length) {
            delText += text.oldWords[j];
            j++;
        }

        // collect consecutive inserts
        while (text.newToOld[i] == null && i < text.newWords.length) {
            insText += text.newWords[i];
            i++;
        }

        // remove leading and trailing similarities betweein delText and ins from highlighting
        var preText = "";
        var postText = "";
        if (wordDiff) {
            if (delText != "" && insText != "") {
                // remove leading similarities
                while (delText.charAt(0) == insText.charAt(0)) {
                    preText = preText + delText.charAt(0);
                    delText = delText.substr(1);
                    insText = insText.substr(1);
                }

                // remove trailing similarities
                while (delText.charAt(delText.length - 1) == insText.charAt(insText.length - 1)) {
                    postText = delText.charAt(delText.length - 1) + postText;
                    delText = delText.substr(0, delText.length - 1);
                    insText = insText.substr(0, insText.length - 1);
                }
            }
        }

        // output the identical text, deletions and inserts
        if (blockText != "") {
            outText += blockText;
        }
        if (identText != "") {
            outText += identStart + Escape(identText) + identEnd;
        }
        outText += preText;
        if (delText != "") {
            delText = htmlDeleteStart + Escape(delText) + htmlDeleteEnd;
            delText = delText.replace(/\n/g, "&para;<br/>");
            outText += delText;
        }
        /*if (insText != '') {
		insText = htmlInsertStart + Escape(insText) + htmlInsertEnd;
		insText = insText.replace(/\n/g, '&para;<br/>');
		outText += insText;
	}*/
        outText += postText;
    } while (i <= text.newWords.length);

    outText += "\n";
    outText = HtmlFormat(outText);

    return outText;
}

// Escape: replaces html-sensitive characters in output text with character entities

function Escape(text) {
    /*text = text.replace(/&/g, "&amp;");
	text = text.replace(/</g, "&lt;");
	text = text.replace(/>/g, "&gt;");
	text = text.replace(/"/g, "&quot;"); //"*/

    return text;
}

// HtmlCustomize: customize indicator html: replace {number} with the block number, {block} with the block style

function HtmlCustomize(text, block) {
    text = text.replace(/\{number\}/, block);
    text = text.replace(/\{block\}/, styleBlock[block]);

    return text;
}

// HtmlFormat: replaces newlines and multiple spaces in text with html code

function HtmlFormat(text) {
    text = text.replace(/  /g, " &nbsp;");
    text = text.replace(/\n/g, "<br/>");

    return text;
}

// DetectBlocks: detect block borders and moved blocks
// input: text object, block object

function DetectBlocks(text, block) {
    block.oldStart = [];
    block.oldToNew = [];
    block.oldLength = [];
    block.oldWords = [];
    block.newStart = [];
    block.newLength = [];
    block.newWords = [];
    block.newNumber = [];
    block.newBlock = [];
    block.newLeft = [];
    block.newRight = [];
    block.newLeftIndex = [];
    block.newRightIndex = [];

    var blockNumber = 0;
    var wordCounter = 0;
    var realWordCounter = 0;

    // get old text block order
    if (showBlockMoves) {
        j = 0;
        i = 0;
        do {
            // detect block boundaries on old text
            if (text.oldToNew[j] != i || blockNumber == 0) {
                if (
                    (text.oldToNew[j] != null || j >= text.oldWords.length) &&
                    (text.newToOld[i] != null || i >= text.newWords.length)
                ) {
                    if (blockNumber > 0) {
                        block.oldLength[blockNumber - 1] = wordCounter;
                        block.oldWords[blockNumber - 1] = realWordCounter;
                        wordCounter = 0;
                        realWordCounter = 0;
                    }

                    if (j >= text.oldWords.length) {
                        j++;
                    } else {
                        i = text.oldToNew[j];
                        block.oldStart[blockNumber] = j;
                        block.oldToNew[blockNumber] = text.oldToNew[j];
                        blockNumber++;
                    }
                }
            }

            // jump over identical pairs
            while (i < text.newWords.length && j < text.oldWords.length) {
                if (text.newToOld[i] == null || text.oldToNew[j] == null) {
                    break;
                }
                if (text.oldToNew[j] != i) {
                    break;
                }
                i++;
                j++;
                wordCounter++;
                if (/\w/.test(text.newWords[i])) {
                    realWordCounter++;
                }
            }

            // jump over consecutive deletions
            while (text.oldToNew[j] == null && j < text.oldWords.length) {
                j++;
            }

            // jump over consecutive inserts
            while (text.newToOld[i] == null && i < text.newWords.length) {
                i++;
            }
        } while (j <= text.oldWords.length);

        // get the block order in the new text
        var lastMin;
        var currMinIndex;
        lastMin = null;

        // sort the data by increasing start numbers into new text block info
        for (i = 0; i < blockNumber; i++) {
            currMin = null;
            for (j = 0; j < blockNumber; j++) {
                curr = block.oldToNew[j];
                if (curr > lastMin || lastMin == null) {
                    if (curr < currMin || currMin == null) {
                        currMin = curr;
                        currMinIndex = j;
                    }
                }
            }
            block.newStart[i] = block.oldToNew[currMinIndex];
            block.newLength[i] = block.oldLength[currMinIndex];
            block.newWords[i] = block.oldWords[currMinIndex];
            block.newNumber[i] = currMinIndex;
            lastMin = currMin;
        }

        // detect not moved blocks
        for (i = 0; i < blockNumber; i++) {
            if (block.newBlock[i] == null) {
                if (block.newNumber[i] == i) {
                    block.newBlock[i] = 0;
                }
            }
        }

        // detect switches of neighbouring blocks
        for (i = 0; i < blockNumber - 1; i++) {
            if (block.newBlock[i] == null && block.newBlock[i + 1] == null) {
                if (block.newNumber[i] - block.newNumber[i + 1] == 1) {
                    if (block.newNumber[i + 1] - block.newNumber[i + 2] != 1 || i + 2 >= blockNumber) {
                        // the shorter one is declared the moved one
                        if (block.newLength[i] < block.newLength[i + 1]) {
                            block.newBlock[i] = 1;
                            block.newBlock[i + 1] = 0;
                        } else {
                            block.newBlock[i] = 0;
                            block.newBlock[i + 1] = 1;
                        }
                    }
                }
            }
        }

        // mark all others as moved and number the moved blocks
        j = 1;
        for (i = 0; i < blockNumber; i++) {
            if (block.newBlock[i] == null || block.newBlock[i] == 1) {
                block.newBlock[i] = j++;
            }
        }

        // check if a block has been moved from this block border
        for (i = 0; i < blockNumber; i++) {
            for (j = 0; j < blockNumber; j++) {
                if (block.newNumber[j] == i) {
                    if (block.newBlock[j] > 0) {
                        // block moved right
                        if (block.newNumber[j] < j) {
                            block.newRight[i] = block.newBlock[j];
                            block.newRightIndex[i] = j;
                        }

                        // block moved left
                        else {
                            block.newLeft[i + 1] = block.newBlock[j];
                            block.newLeftIndex[i + 1] = j;
                        }
                    }
                }
            }
        }
    }
    return;
}

function comparerAction(html) {
    var result = "";
    var version1 = html.children(".content").first().text();
    var version2 = html.children(".content").last().text();
    result = result + StringDiff2(version1, version2);

    html.children(".content").attr("tabindex", "0");

    html.children(".content")
        .first()
        .html("<p>" + result + "</p>");

    var result2 = "";
    result2 = result2 + StringDiff1(version1, version2);

    html.children(".content")
        .last()
        .html("<p>" + result2 + "</p>");

    html.children(".header").replaceWith($('<h2 class="header">' + this.innerHTML + "</h2>"));

    return html;
}
