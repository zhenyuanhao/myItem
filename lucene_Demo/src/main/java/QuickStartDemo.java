import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

public class QuickStartDemo {

    /**
     * 创建索引过程
     */
    @Test
    public void testCreateIndex() throws Exception {
        // 创建document对象
        Document doc =new Document();
        // 添加域
        doc.add(new StringField("id","3",Field.Store.YES));
        doc.add(new TextField("name","去哪里旅游",Field.Store.YES));
        doc.add(new TextField("context","南京",Field.Store.YES));
        // 指定索引生成的位置
        File file =new File("F:\\index");
        FSDirectory directory = FSDirectory.open(file);

        Analyzer analyzer =new IKAnalyzer();
        // 指定配置信息
        IndexWriterConfig indexWriterConfig =new IndexWriterConfig(Version.LATEST,analyzer);
        // 创建建立索引对象
        IndexWriter indexWriter =new IndexWriter(directory,indexWriterConfig);
        indexWriter.addDocument(doc);
        indexWriter.close();
    }


    /**
     * 检索
     * @throws Exception
     */
    @Test
    public void testSearchIndex() throws Exception {
        // 定义检索内容
        String text ="去哪里";
        // 将检索的内容转成query对象
        Analyzer analyzer =new IKAnalyzer();
        QueryParser queryParser =new QueryParser("name",analyzer);
        Query query = queryParser.parse(text);
        // 创建检索对象
        File file =new File("F:\\index");
        Directory directory =FSDirectory.open(file);
        IndexReader indexReader =DirectoryReader.open(directory);
        IndexSearcher indexSearcher =new IndexSearcher(indexReader);
        // 查询
        TopDocs topDocs = indexSearcher.search(query, Integer.MAX_VALUE);
        System.out.println("获取到的总条数"+topDocs.totalHits);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            Document doc = indexSearcher.doc(docId);
            System.out.println(doc.get("id"));
            System.out.println(doc.get("name"));
            System.out.println(doc.get("context"));
        }
    }

    @Test
    public void testUpdateDoc() throws IOException {
        // 创建IndexWriter
        File file =new File("F:\\index");
        Directory directory =FSDirectory.open(file);
        IndexWriterConfig indexWriterConfig =new IndexWriterConfig(Version.LATEST,new IKAnalyzer());
        IndexWriter indexWriter =new IndexWriter(directory,indexWriterConfig);
        // 更新索引库
        Term term =new Term("id","2");
        Document doc =new Document();
        doc.add(new StringField("id","2", Field.Store.YES));
        doc.add(new TextField("title","c++学科", Field.Store.YES));
        doc.add(new TextField("content", "太难了", Field.Store.YES));
        indexWriter.updateDocument(term,doc);
        indexWriter.close();
    }


        // 删除索引库
    @Test
    public void testDelete() throws IOException {
        // 创建IndexWriter
        File file =new File("F:\\index");
        Directory directory =FSDirectory.open(file);
        IndexWriterConfig indexWriterConfig =new IndexWriterConfig(Version.LATEST,new IKAnalyzer());
        IndexWriter indexWriter =new IndexWriter(directory,indexWriterConfig);
        //Term term1 =new Term("id","2");
        //Term term2 =new Term("id","3");
        indexWriter.deleteAll();
        //indexWriter.deleteDocuments(term1,term2);
        indexWriter.close();
    }



}
