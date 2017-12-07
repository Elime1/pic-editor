package io.github.elime1.piceditor.utils;

public class ImageIndex {
    private int numberOfImages;
    private int currentIndex;

    public ImageIndex(int numberOfImages, int currentIndex) {
        this.numberOfImages = numberOfImages;
        this.currentIndex = currentIndex;
    }

    public int currentIndex() {
        return currentIndex;
    }

    public int currentImageNumber() {
        return currentIndex + 1;
    }

    public void setCurrentIndex(int index) {
        if (index >= 0 && index < numberOfImages) {
            currentIndex = index;
        }
    }

    public int nexImage() {
        if (isLast(currentIndex)) {
            return currentIndex = 0;
        }
        return ++currentIndex;
    }

    public int previousImage() {
        if (isFirst(currentIndex)) {
            return currentIndex = numberOfImages - 1;
        }
        return --currentIndex;
    }

    private boolean isFirst(int index) {
        return index == 0;
    }

    private boolean isLast(int index) {
        return index == numberOfImages - 1;
    }
}
