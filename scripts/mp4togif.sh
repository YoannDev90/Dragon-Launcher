ffmpeg -i ./*.mp4 -vf "fps=30,scale=1216:2688:flags=neighbor,palettegen" palette.png
ffmpeg -i ./*.mp4 -i palette.png -lavfi "fps=30,scale=1216:2688:flags=neighbor[x];[x][1:v]paletteuse=dither=none" demo.gif
