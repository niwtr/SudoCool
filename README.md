# SudoCool -- Webcam Sudoku Recognizer & Solver

![](https://github.com/niwtr/SudoCool/blob/master/Screenshots/ss0.png)

Let me introduce this in general.

This is originally an assignment project of lesson "Machine Intelligence" in Beijing University of Posts & Communications (BUPT) but now I'm making it open-source. I learned a lot from other projects available on Github and I also want to share my knowledges. So here it goes.

Our major developers are:
Tianrui Niu (niwtr)
Han Liu (laddie 132)
Jiahui Liu (shunvforever)


## A simple introduction
This is a Webcam Sudoku Recognizer & Solver based on OpenCV 2.4.13 and BPNN. You use your webcam to capture sudoku puzzles on flat platforms (You can either use papers, iPads or your smartphones.) and this one will solve your puzzles "automagically". The solution will be printed on the same screen. You needn't perform any operations during that period since our software will do that for you. It automatically captures the video flow, locates the puzzle, recognizes the numbers and solves them.

When no puzzle's found in the TV, it displays "Targeting..." and keep searching for the puzzle boards.
![](https://github.com/niwtr/SudoCool/blob/master/Screenshots/ss4.png)

When a puzzle board is found in TV and it's not at that time able to solve that puzzle, displays "Scanning...".
![](https://github.com/niwtr/SudoCool/blob/master/Screenshots/ss6.png)

When the puzzle is solved the solution will be displayed on both TV screen and solution board on the right.
![](https://github.com/niwtr/SudoCool/blob/master/Screenshots/ss1.png)

 After you get the solution you are also allowed to export your solution in PDF format.

This picture demonstrates that the software has recognized & solved a sudoku puzzle which is projected on my New iPad.
![](https://github.com/niwtr/SudoCool/blob/master/Screenshots/ss2.png)

And this demonstrates the whole recognition procedure:

![](https://github.com/niwtr/SudoCool/blob/master/Screenshots/ss3.gif)

This software comes in both source code and precompiled jar packet. You can first take a try before you dive into the code.

## How it works
The basic algorithm of number recognition is BP neural networks. In our precompiled jar package the neural networks is trained from both handwriting and printed numbers so the software performs well on both hand-written and printed sudoku. The NN is saved on JSON files and you can check that if you like. Note that we currently do not support users to train their own networks but you may modify the code to achieve that. Sorry.

The project splits into four major parts:

* **Eye**: Controls the webcam (TV), collects raw video flow.

* **ImgWorks**: Process each frames of the raw video flow, extracts features, locates the puzzles, splits the puzzles and extract each numbers. This also handles the output images. The operation sequence can be seen in this piece of code which is in Recognizer.java: 
```this
                .getImg(img)
                .preProcessImg()
                .extractOuterBound()
                .recognizeNumbers()
                .arrangeNumbersMatrix()
                .solveNumbers()
                .drawText()
                .drawOuterBound()
                .drawRecognizedNumbers();
```

The image processing algorithm is easy to understand. When a webcam frame is sent to the Recognizer module, it converts the image to binary and find the largest outline (say, contour). If that outline is almost square, we recon that to be a game board. Then we split that contour into several small parts (ex. 81 for 9x9 sudoku, 64 for 8x8 sudoku) and perform after-processing for each parts, cuts them into smaller ones and extract the features. These will be arranged to an ordered Java matrix. After that, those 28x28 binary images, or features, will be sent to the BP neural network for number recognizing. When the sudoku is solved we also need to project the answer to the TV, which brings us a taste of "Augmented Reality".

* **Identifier**: Identifies the numbers extracted by ImgWorks and recognizes the numbers (picture -> text). This part also contains the BP neural network algorithm. You can replace this one with another NN if you like.
* **Solver**: Contains fast sudoku solving algorithm. It solves the sudoku after numbers are recognized. Note that if the solving algorithm is only performed on each frame, regardless of the history, in rare chance could we find the solution, cuz a single failure to correctly recognize a number would ruin the whole solution. For that cause we will try to remember all the occurs of numbers on each cell and traverse all the possibilities. This will boost our memory & CPU usage but guarantees that the solution work is done in a relatively short time.


If you are here seeking for BPNN, go for **Identifier**, or is you are seeking for image processing techniques, watch for **ImgWorks**. 



Hack and glory awaits!

Tianrui Niu (niwtr)