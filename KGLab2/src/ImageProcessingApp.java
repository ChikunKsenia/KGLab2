import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;


public class ImageProcessingApp extends JFrame {

    private BufferedImage originalImage;
    private BufferedImage processedImage;

    private JButton chooseImageButton;
    private JButton negativeButton;
    private JButton addConstantButton;
    private JButton subtractConstantButton;
    private JButton linearContrastButton;
    private JButton erodeButton;
    private JButton dilateButton;

    private JPanel imagePanel;
    private JLabel originalImageLabel;
    private JLabel processedImageLabel;

    private JTextField constantTextField;


    public ImageProcessingApp() {
        // Настройка окна приложения
        setTitle("Image Processing App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLayout(new BorderLayout());

        // Создание кнопки выбора изображения
        chooseImageButton = new JButton("Выбрать изображение");
        chooseImageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "png");
                fileChooser.setFileFilter(filter);
                int option = fileChooser.showOpenDialog(ImageProcessingApp.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        originalImage = ImageIO.read(selectedFile);
                        processedImage = originalImage;

                        imagePanel = new JPanel(new GridLayout(1, 2));
                        originalImageLabel = new JLabel(new ImageIcon(originalImage));
                        processedImageLabel = new JLabel(new ImageIcon(processedImage));
                        imagePanel.add(originalImageLabel);
                        imagePanel.add(processedImageLabel);

                        constantTextField.setEnabled(true);

                        add(imagePanel, BorderLayout.CENTER);
                        revalidate();
                        repaint();
                    } catch (IOException exception) {
                        JOptionPane.showMessageDialog(ImageProcessingApp.this,
                                "Не удалось загрузить изображение.", "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
            add(chooseImageButton, BorderLayout.NORTH);

        // Создание кнопок
        negativeButton = new JButton("Негатив");
        negativeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processedImage = applyNegative(originalImage);
                updateImageLabel();
            }
        });

        addConstantButton = new JButton("Прибавить константу");
        addConstantButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int constant = Integer.parseInt(constantTextField.getText());
                processedImage = addConstant(originalImage, constant);
                updateImageLabel();
            }
        });

        subtractConstantButton = new JButton("Вычесть константу");
        subtractConstantButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int constant = Integer.parseInt(constantTextField.getText());
                processedImage = subtractConstant(originalImage, constant);
                updateImageLabel();
            }
        });

        linearContrastButton = new JButton("Линейное контрастирование");
        linearContrastButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processedImage = applyLinearContrast(originalImage);
                updateImageLabel();
            }
        });

        erodeButton = new JButton("Эрозия");
        erodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processedImage = applyErosion(originalImage);
                updateImageLabel();
            }
        });

        dilateButton = new JButton("Дилатация");
        dilateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processedImage = applyDilation(originalImage);
                updateImageLabel();
            }
        });


        // Создание панели с изображением
        imagePanel = new JPanel();
        originalImageLabel = new JLabel();
        processedImageLabel= new JLabel();
        imagePanel.add(originalImageLabel);
        imagePanel.add(processedImageLabel);

        // Создание текстового поля для константы
        constantTextField = new JTextField(10);

        // Добавление компонентов на форму
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(chooseImageButton);
        buttonPanel.add(negativeButton);
        buttonPanel.add(addConstantButton);
        buttonPanel.add(subtractConstantButton);
        buttonPanel.add(linearContrastButton);
        buttonPanel.add(constantTextField);
        buttonPanel.add(erodeButton);  // Added erode button
        buttonPanel.add(dilateButton); // Added dilate button

       add(buttonPanel, BorderLayout.NORTH);
        add(imagePanel, BorderLayout.CENTER);

        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        getContentPane().add(imagePanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void updateImageLabel() {
        ImageIcon originalIcon = new ImageIcon(originalImage);
        ImageIcon processedIcon = new ImageIcon(processedImage);
        originalImageLabel.setIcon(originalIcon);
        processedImageLabel.setIcon(processedIcon);
    }

    private BufferedImage applyErosion(BufferedImage image) {
        int size = 3; // Размер ядра для эрозии
        float[] kernelData = new float[size * size];
        for (int i = 0; i < size * size; i++) {
            kernelData[i] = 1.0f;
        }

        Kernel kernel = new Kernel(size, size, kernelData);
        ConvolveOp convolve = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);
        return convolve.filter(image, null);
    }

    private BufferedImage applyDilation(BufferedImage image) {
        int size = 3; // Размер ядра для дилатации
        float[] kernelData = new float[size * size];
        for (int i = 0; i < size * size; i++) {
            kernelData[i] = 1.0f;
        }

        Kernel kernel = new Kernel(size, size, kernelData);
        ConvolveOp convolve = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return convolve.filter(image, null);
    }

    // Метод для применения негатива к изображению
    private BufferedImage applyNegative(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage negativeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = 255 - (rgb >> 16) & 0xFF;
                int g = 255 - (rgb >> 8) & 0xFF;
                int b = 255 - rgb & 0xFF;
                int negativeRGB = (r << 16) | (g << 8) | b;
                negativeImage.setRGB(x, y, negativeRGB);
            }
        }

        return negativeImage;
    }

    // Метод для прибавления константы к изображению
    private BufferedImage addConstant(BufferedImage image, int constant) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                r += constant;
                g += constant;
                b += constant;

                r = Math.min(Math.max(0, r), 255);
                g = Math.min(Math.max(0, g), 255);
                b = Math.min(Math.max(0, b), 255);

                int resultRGB = (r << 16) | (g << 8) | b;
                resultImage.setRGB(x, y, resultRGB);
            }
        }

        return resultImage;
    }

    // Метод для вычитания константы из изображения
    private BufferedImage subtractConstant(BufferedImage image, int constant) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                r -= constant;
                g -= constant;
                b -= constant;

                r = Math.min(Math.max(0, r), 255);
                g = Math.min(Math.max(0, g), 255);
                b = Math.min(Math.max(0, b), 255);

                int resultRGB = (r << 16) | (g << 8) | b;
                resultImage.setRGB(x, y, resultRGB);
            }
        }

        return resultImage;
    }

    // Метод для применения линейного контрастирования к изображению
    private BufferedImage applyLinearContrast(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int minIntensity = 255;
        int maxIntensity = 0;

        // Поиск минимальной и максимальной интенсивности пикселей
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int intensity = (rgb >> 16) & 0xFF;
                minIntensity = Math.min(minIntensity, intensity);
                maxIntensity = Math.max(maxIntensity, intensity);
            }
        }

        // Применение линейного контрастирования
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int intensity = (rgb >> 16) & 0xFF;
                int newIntensity = (int) (255.0 * (intensity - minIntensity) / (maxIntensity - minIntensity));
                int resultRGB = (newIntensity << 16) | (newIntensity << 8) | newIntensity;
                resultImage.setRGB(x, y, resultRGB);
            }
        }

        return resultImage;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ImageProcessingApp().setVisible(true);

            }
        });
    }

}
