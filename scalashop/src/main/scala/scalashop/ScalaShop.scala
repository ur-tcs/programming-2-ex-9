package scalashop

import org.scalameter.*
import scalashop.image.Kernel

import java.awt.*
import java.awt.event.*
import javax.swing.JPopupMenu.Separator
import javax.swing.*
import javax.swing.event.*
import javax.swing.plaf.basic.BasicSeparatorUI
import javax.swing.table.AbstractTableModel
import javax.swing.table.DefaultTableColumnModel
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableModel
import scala.collection.mutable.ArrayBuffer

object ScalaShop:

  class ScalaShopFrame extends JFrame("ScalaShop\u2122"):
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    setSize(1024, 600)
    setLayout(BorderLayout())

    val rightpanel = JPanel()
    rightpanel.setBorder(
      BorderFactory.createEtchedBorder(border.EtchedBorder.LOWERED)
    )
    rightpanel.setLayout(BorderLayout())
    add(rightpanel, BorderLayout.EAST)

    val controls = JPanel()
    controls.setLayout(GridLayout(0, 2, 0, 10))
    rightpanel.add(controls, BorderLayout.NORTH)

    val filterLabel = JLabel("Filter")
    controls.add(filterLabel)

    val filterCombo = JComboBox(
      Array(
        "black-and-white",
        "red-splash",
        "simple-blur",
        "blur",
        "box-blur",
        "gaussian-blur",
        "edge-detection"
      )
    )
    controls.add(filterCombo)

    val radiusLabel = JLabel("Radius")
    controls.add(radiusLabel)
    radiusLabel.setVisible(false)

    val radiusSpinner = JSpinner(SpinnerNumberModel(3, 1, 16, 1))
    controls.add(radiusSpinner)
    radiusSpinner.setVisible(false)

    val kernel1Label = JLabel("KernelX")
    controls.add(kernel1Label)
    val model1 = new DefaultTableModel(3, 3)
    val kernel1 = new JTable(model1)
    controls.add(kernel1)
    setKernel(kernel1, Kernel.uniform(3))
    kernel1Label.setVisible(false)
    kernel1.setVisible(false)

    val kernel2Label = JLabel("KernelY")
    controls.add(kernel2Label)
    val model2 = new DefaultTableModel(3, 3)
    val kernel2 = new JTable(model2)
    controls.add(kernel2)
    kernel2Label.setVisible(false)
    kernel2.setVisible(false)
    setKernel(kernel2, Kernel.uniform(3))

    filterCombo.addActionListener(new ActionListener():
      def actionPerformed(e: ActionEvent): Unit = getFilterName match
        case "simple-blur" =>
          radiusLabel.setVisible(false)
          radiusSpinner.setVisible(false)
          kernel1Label.setVisible(false)
          kernel1.setVisible(false)
          kernel2Label.setVisible(false)
          kernel2.setVisible(false)
        case "blur" =>
          radiusLabel.setVisible(false)
          radiusSpinner.setVisible(false)
          kernel1Label.setVisible(true)
          kernel1.setVisible(true)
          kernel2Label.setVisible(false)
          kernel2.setVisible(false)

          setKernel(kernel1, Kernel.uniform(3))
        case "box-blur" =>
          radiusLabel.setVisible(true)
          radiusSpinner.setVisible(true)
          kernel1Label.setVisible(false)
          kernel1.setVisible(false)
          kernel2Label.setVisible(false)
          kernel2.setVisible(false)
        case "gaussian-blur" =>
          radiusLabel.setVisible(false)
          radiusSpinner.setVisible(false)
          kernel1Label.setVisible(false)
          kernel1.setVisible(false)
          kernel2Label.setVisible(false)
          kernel2.setVisible(false)
        case "black-and-white" =>
          radiusLabel.setVisible(false)
          radiusSpinner.setVisible(false)
          kernel1Label.setVisible(false)
          kernel1.setVisible(false)
          kernel2Label.setVisible(false)
          kernel2.setVisible(false)
        case "red-splash" =>
          radiusLabel.setVisible(false)
          radiusSpinner.setVisible(false)
          kernel1Label.setVisible(false)
          kernel1.setVisible(false)
          kernel2Label.setVisible(false)
          kernel2.setVisible(false)
        case "edge-detection" =>
          radiusLabel.setVisible(false)
          radiusSpinner.setVisible(false)
          kernel1Label.setVisible(true)
          kernel1.setVisible(true)
          kernel2Label.setVisible(true)
          kernel2.setVisible(true)
        case "median-filter" =>
          radiusLabel.setVisible(true)
          radiusSpinner.setVisible(true)
          kernel1Label.setVisible(false)
          kernel1.setVisible(false)
          kernel2Label.setVisible(false)
          kernel2.setVisible(false)
    );
    val tasksLabel = JLabel("Tasks")
    controls.add(tasksLabel)

    val tasksSpinner = JSpinner(SpinnerNumberModel(1, 1, 128, 1))
    controls.add(tasksSpinner)

    val stepbutton = JButton("Apply filter")
    stepbutton.addActionListener(new ActionListener:
      def actionPerformed(e: ActionEvent): Unit =
        val time = measure {
          canvas.applyFilter(
            getFilterName,
            getNumTasks,
            getRadius,
            getKernel1,
            getKernel2
          )
        }
        updateInformationBox(time.value.toString())
    )
    controls.add(stepbutton)

    val clearButton = JButton("Reload")
    clearButton.addActionListener(new ActionListener:
      def actionPerformed(e: ActionEvent): Unit =
        canvas.reload()
    )
    controls.add(clearButton)

    val info = JTextArea("   ")
    info.setBorder(BorderFactory.createLoweredBevelBorder)
    rightpanel.add(info, BorderLayout.SOUTH)

    val mainMenuBar = JMenuBar()

    val fileMenu = JMenu("File")
    val openMenuItem = JMenuItem("Open...")
    openMenuItem.addActionListener(new ActionListener:
      def actionPerformed(e: ActionEvent): Unit =
        val fc = JFileChooser()
        if fc.showOpenDialog(ScalaShopFrame.this) == JFileChooser.APPROVE_OPTION
        then
          canvas.loadFile(fc.getSelectedFile.getPath)
    )
    fileMenu.add(openMenuItem)
    val exitMenuItem = JMenuItem("Exit")
    exitMenuItem.addActionListener(new ActionListener:
      def actionPerformed(e: ActionEvent): Unit =
        sys.exit(0)
    )
    fileMenu.add(exitMenuItem)

    mainMenuBar.add(fileMenu)

    val helpMenu = JMenu("Help")
    val aboutMenuItem = JMenuItem("About")
    aboutMenuItem.addActionListener(new ActionListener:
      def actionPerformed(e: ActionEvent): Unit =
        JOptionPane.showMessageDialog(
          null,
          "ScalaShop, the ultimate image manipulation tool\nBrought to you by EPFL, 2015-2023"
        )
    )
    helpMenu.add(aboutMenuItem)

    mainMenuBar.add(helpMenu)

    setJMenuBar(mainMenuBar)

    val canvas = PhotoCanvas()

    val scrollPane = JScrollPane(canvas)

    add(scrollPane, BorderLayout.CENTER)
    setVisible(true)

    def updateInformationBox(time: String): Unit =
      info.setText(s"Time: $time")

    def getNumTasks: Int = tasksSpinner.getValue.asInstanceOf[Int]

    def getRadius: Int = radiusSpinner.getValue.asInstanceOf[Int]

    def getFilterName: String =
      filterCombo.getSelectedItem.asInstanceOf[String]

    def getKernel(kernel: JTable) =
      val data = new Array[Float](9)
      for r <- 0 until kernel.getRowCount do
        for c <- 0 until kernel.getColumnCount() do
          val optValue =
            kernel.getValueAt(r, c).asInstanceOf[String].toFloatOption
          val value = optValue match
            case None    => 0f
            case Some(f) => f
          data(r + 3 * c) = value
      Kernel(3, 3, data)

    def getKernel1 = getKernel(kernel1)
    def getKernel2 = getKernel(kernel2)

    def setKernel(kernelTable: JTable, kernel: Kernel) =
      val arrData = kernel.data
      kernelTable.setValueAt(arrData(0).toString(), 0, 0)
      kernelTable.setValueAt(arrData(1).toString(), 0, 1)
      kernelTable.setValueAt(arrData(2).toString(), 0, 2)
      kernelTable.setValueAt(arrData(3).toString(), 1, 0)
      kernelTable.setValueAt(arrData(4).toString(), 1, 1)
      kernelTable.setValueAt(arrData(5).toString(), 1, 2)
      kernelTable.setValueAt(arrData(6).toString(), 2, 0)
      kernelTable.setValueAt(arrData(7).toString(), 2, 1)
      kernelTable.setValueAt(arrData(8).toString(), 2, 2)

  try UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
  catch
    case _: Exception =>
      println("Cannot set look and feel, using the default one.")

  val frame = ScalaShopFrame()

  def main(args: Array[String]): Unit =
    frame.repaint()
