package com.example.collegeplacementtracker

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PDFReportGenerator(private val context: Context) {

    private val colorPurple = BaseColor(193, 155, 255)
    private val colorPink = BaseColor(246, 178, 255)

    fun generateDepartmentReport(
        departmentName: String,
        totalStudents: Int,
        eligibleStudents: Int,
        placedStudents: Int,
        highestPackage: Double,
        averagePackage: Double
    ): String? {
        try {
            val fileName = "Department_Report_${System.currentTimeMillis()}.pdf"
            val filePath = createPDFFile(fileName)

            val document = Document(PageSize.A4)
            PdfWriter.getInstance(document, FileOutputStream(filePath))
            document.open()

            // Title
            addTitle(document, "DEPARTMENT PLACEMENT REPORT")
            addSubtitle(document, departmentName)
            addSpacer(document)

            // Date
            val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            val datePara = Paragraph(
                "Generated: $currentDate",
                Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.GRAY)
            )
            datePara.alignment = Element.ALIGN_RIGHT
            document.add(datePara)
            addSpacer(document)

            // Summary Table
            val table = PdfPTable(2)
            table.widthPercentage = 100f
            table.setWidths(floatArrayOf(1f, 1f))

            addHeaderCell(table, "METRIC")
            addHeaderCell(table, "VALUE")

            addDataCell(table, "Total Students")
            addDataCell(table, totalStudents.toString())

            addDataCell(table, "Eligible Students")
            addDataCell(table, eligibleStudents.toString())

            addDataCell(table, "Placed Students")
            addDataCell(table, placedStudents.toString())

            addDataCell(table, "Placement Rate")
            val placementRate = if (eligibleStudents > 0)
                (placedStudents * 100.0 / eligibleStudents) else 0.0
            addDataCell(table, String.format("%.1f%%", placementRate))

            addDataCell(table, "Highest Package")
            addDataCell(table, "₹${highestPackage} LPA")

            addDataCell(table, "Average Package")
            addDataCell(table, "₹${averagePackage} LPA")

            document.add(table)
            addSpacer(document)

            // Footer
            addFooter(document)

            document.close()

            Toast.makeText(context, "Report saved: $fileName", Toast.LENGTH_LONG).show()
            return filePath

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    fun generateStudentListReport(students: List<User>): String? {
        try {
            val fileName = "Student_List_${System.currentTimeMillis()}.pdf"
            val filePath = createPDFFile(fileName)

            val document = Document(PageSize.A4)
            PdfWriter.getInstance(document, FileOutputStream(filePath))
            document.open()

            addTitle(document, "STUDENT LIST REPORT")
            addSpacer(document)

            // Date
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val datePara = Paragraph(
                "Generated: ${dateFormat.format(Date())}",
                Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.GRAY)
            )
            datePara.alignment = Element.ALIGN_RIGHT
            document.add(datePara)
            addSpacer(document)

            // Student Table
            val table = PdfPTable(5)
            table.widthPercentage = 100f
            table.setWidths(floatArrayOf(1f, 2f, 2f, 1f, 1f))

            addHeaderCell(table, "S.No")
            addHeaderCell(table, "Name")
            addHeaderCell(table, "Email")
            addHeaderCell(table, "CGPA")
            addHeaderCell(table, "Branch")

            students.forEachIndexed { index, student ->
                addDataCell(table, (index + 1).toString())
                addDataCell(table, student.fullName)
                addDataCell(table, student.email)
                addDataCell(table, student.cgpa?.toString() ?: "N/A")
                addDataCell(table, student.branch.toString())
            }

            document.add(table)
            addSpacer(document)
            addFooter(document)

            document.close()

            Toast.makeText(context, "Report saved: $fileName", Toast.LENGTH_LONG).show()
            return filePath

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    fun generatePlacedStudentsReport(applications: List<Application>): String? {
        try {
            val fileName = "Placed_Students_${System.currentTimeMillis()}.pdf"
            val filePath = createPDFFile(fileName)

            val document = Document(PageSize.A4)
            PdfWriter.getInstance(document, FileOutputStream(filePath))
            document.open()

            addTitle(document, "PLACED STUDENTS REPORT")
            addSpacer(document)

            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val datePara = Paragraph(
                "Generated: ${dateFormat.format(Date())}",
                Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.GRAY)
            )
            datePara.alignment = Element.ALIGN_RIGHT
            document.add(datePara)
            addSpacer(document)

            val table = PdfPTable(4)
            table.widthPercentage = 100f
            table.setWidths(floatArrayOf(1f, 2f, 2f, 2f))

            addHeaderCell(table, "S.No")
            addHeaderCell(table, "Student ID")
            addHeaderCell(table, "Company ID")
            addHeaderCell(table, "Status")

            applications.forEachIndexed { index, app ->
                addDataCell(table, (index + 1).toString())
                addDataCell(table, app.studentId.toString())
                addDataCell(table, app.companyId.toString())
                addDataCell(table, app.status)
            }

            document.add(table)
            addSpacer(document)
            addFooter(document)

            document.close()

            Toast.makeText(context, "Report saved: $fileName", Toast.LENGTH_LONG).show()
            return filePath

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    fun generateApplicationStatusReport(applications: List<Application>): String? {
        try {
            val fileName = "Application_Status_${System.currentTimeMillis()}.pdf"
            val filePath = createPDFFile(fileName)

            val document = Document(PageSize.A4)
            PdfWriter.getInstance(document, FileOutputStream(filePath))
            document.open()

            addTitle(document, "APPLICATION STATUS REPORT")
            addSpacer(document)

            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val datePara = Paragraph(
                "Generated: ${dateFormat.format(Date())}",
                Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.GRAY)
            )
            datePara.alignment = Element.ALIGN_RIGHT
            document.add(datePara)
            addSpacer(document)

            // Summary
            val pendingCount = applications.count { it.status == ApplicationStatus.PENDING }
            val selectedCount = applications.count { it.status == ApplicationStatus.SELECTED }
            val rejectedCount = applications.count { it.status == ApplicationStatus.REJECTED }
            val shortlistedCount = applications.count { it.status == ApplicationStatus.SHORTLISTED }

            val summaryTable = PdfPTable(2)
            summaryTable.widthPercentage = 50f
            addHeaderCell(summaryTable, "Status")
            addHeaderCell(summaryTable, "Count")
            addDataCell(summaryTable, "Pending")
            addDataCell(summaryTable, pendingCount.toString())
            addDataCell(summaryTable, "Selected")
            addDataCell(summaryTable, selectedCount.toString())
            addDataCell(summaryTable, "Rejected")
            addDataCell(summaryTable, rejectedCount.toString())
            addDataCell(summaryTable, "Shortlisted")
            addDataCell(summaryTable, shortlistedCount.toString())
            addDataCell(summaryTable, "Total")
            addDataCell(summaryTable, applications.size.toString())

            document.add(summaryTable)
            addSpacer(document)
            addFooter(document)

            document.close()

            Toast.makeText(context, "Report saved: $fileName", Toast.LENGTH_LONG).show()
            return filePath

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    private fun createPDFFile(fileName: String): String {
        val folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "PlacementReports"
        )
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return File(folder, fileName).absolutePath
    }

    private fun addTitle(document: Document, title: String) {
        val titleFont = Font(Font.FontFamily.HELVETICA, 20f, Font.BOLD, colorPurple)
        val titlePara = Paragraph(title, titleFont)
        titlePara.alignment = Element.ALIGN_CENTER
        document.add(titlePara)
    }

    private fun addSubtitle(document: Document, subtitle: String) {
        val subtitleFont = Font(Font.FontFamily.HELVETICA, 14f, Font.NORMAL, BaseColor.GRAY)
        val subtitlePara = Paragraph(subtitle, subtitleFont)
        subtitlePara.alignment = Element.ALIGN_CENTER
        document.add(subtitlePara)
    }

    private fun addSpacer(document: Document) {
        document.add(Paragraph(" "))
    }

    private fun addHeaderCell(table: PdfPTable, text: String) {
        val cell = PdfPCell(
            Phrase(
                text,
                Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD, BaseColor.WHITE)
            )
        )
        cell.backgroundColor = colorPurple
        cell.setPadding(8f)
        cell.horizontalAlignment = Element.ALIGN_CENTER
        table.addCell(cell)
    }

    private fun addDataCell(table: PdfPTable, text: String) {
        val cell = PdfPCell(
            Phrase(
                text,
                Font(Font.FontFamily.HELVETICA, 9f, Font.NORMAL, BaseColor.BLACK)
            )
        )
        cell.setPadding(6f)
        cell.horizontalAlignment = Element.ALIGN_LEFT
        table.addCell(cell)
    }

    private fun addFooter(document: Document) {
        val footer = Paragraph(
            "Generated by College Placement Tracker System",
            Font(Font.FontFamily.HELVETICA, 8f, Font.ITALIC, BaseColor.GRAY)
        )
        footer.alignment = Element.ALIGN_CENTER
        document.add(footer)
    }
}