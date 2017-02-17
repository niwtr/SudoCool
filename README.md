# SudoCool Readme
Let me introduce this software in general.

This is originally an assignment project of lesson "Machine Intelligence" in Beijing University of Posts & Communications (BUPT) but now I'm making it open-source. I learned a lot from other projects available on Github and I also want to share my knowledges. So here it goes.

This is a Webcam Sudoku Recognizer & Solver based on OpenCV 2.4.13 and BPNN. You use your webcam to capture sudoku puzzles on flat platforms (You can either use papers, iPads or your smartphones.) and this one will solve your puzzles "automagically". The solution will be printed on the same screen. You needn't perform any operations during that period since our software will do that for you. It automatically captures the video flow, locates the puzzle, recognizes the numbers and solves them. After you get the solution you are also allowed to export your solution in PDF format.

This software comes in both source code and precompiled jar packet. You can first take a try before you dive into the code.

The basic algorithm of number recognition is BP neural networks. In our precompiled jar package the neural networks is trained from both handwriting and printed numbers so the software performs well on both hand-written and printed sudoku. The NN is saved on JSON files and you can check that if you like. Note that we currently do not support users to train their own networks but you may modify the code to achieve that. Sorry.

