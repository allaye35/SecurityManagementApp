import React, { useState, useEffect, useRef } from "react";
import { useParams, Link } from "react-router-dom";
import ContratDeTravailService from "../../services/ContratDeTravailService";
import ArticleContratTravailService from "../../services/ArticleContratTravailService";
import FicheDePaieService from "../../services/FicheDePaieService";
import AgentService from "../../services/AgentService";
import EntrepriseService from "../../services/EntrepriseService";
import { usePDF } from "react-to-pdf";
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';
import "../../styles/ContratDetail.css";
import "../../styles/ContratDetailPrint.css";

const ContratDeTravailDetail = () => {
    const { id } = useParams();
    const [contrat, setContrat] = useState(null);
    const [agent, setAgent] = useState(null);
    const [entreprise, setEntreprise] = useState(null);
    const [articles, setArticles] = useState([]);
    const [fichesDePaie, setFichesDePaie] = useState([]);
    const [loading, setLoading] = useState(true);    const [error, setError] = useState(null);
    const [showPdfPreview, setShowPdfPreview] = useState(false);
    const [exportInProgress, setExportInProgress] = useState(false);
    
    const [showExportOptions, setShowExportOptions] = useState(false);
    const [exportOptions, setExportOptions] = useState({
        includeArticles: true,
        includeFichesDePaie: true,
        includeHeader: true,
        includeFooter: true,
        pageLayout: 'portrait'
    });
    const [showExportLoader, setShowExportLoader] = useState(false);
    const { toPDF, targetRef } = usePDF({
        filename: `contrat-de-travail-${id}.pdf`,
        page: { 
            margin: 20,
            orientation: exportOptions.pageLayout 
        },
        method: 'save'
    });
    
    const handleExportOptionChange = (e) => {
        const { name, checked, value, type } = e.target;
        setExportOptions(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };
    
    const handlePrint = () => {
        window.print();
    };
    
    const toggleExportOptions = () => {
        setShowExportOptions(prev => !prev);
    };

    const exportToPDF = () => {
        setExportInProgress(true);
        setShowExportLoader(true);
        
        if (!exportOptions.includeArticles) {
            document.querySelectorAll('.articles-section').forEach(el => {
                el.style.display = 'none';
            });
        }
        
        if (!exportOptions.includeFichesDePaie) {
            document.querySelectorAll('.fiches-paie-section').forEach(el => {
                el.style.display = 'none';
            });
        }
        
        const element = targetRef.current;
        const fileName = `contrat-travail-${contrat?.referenceContrat || id}.pdf`;
        
        const pdfOptions = {
            margin: 10,
            filename: fileName,
            image: { type: 'jpeg', quality: 0.98 },
            html2canvas: { scale: 2, useCORS: true, logging: false },
            jsPDF: { 
                unit: 'mm', 
                format: 'a4', 
                orientation: exportOptions.pageLayout
            }
        };
        
        html2canvas(element, {
            scale: 2,
            useCORS: true,
            logging: false,
            allowTaint: true
        }).then(canvas => {
            const imgData = canvas.toDataURL('image/jpeg', 0.98);
            const pdf = new jsPDF(
                exportOptions.pageLayout, 
                'mm', 
                'a4'
            );
            const pdfWidth = pdf.internal.pageSize.getWidth();
            const pdfHeight = pdf.internal.pageSize.getHeight();
            const imgWidth = canvas.width;
            const imgHeight = canvas.height;
            const ratio = Math.min(pdfWidth / imgWidth, pdfHeight / imgHeight);
            const imgX = (pdfWidth - imgWidth * ratio) / 2;
            const imgY = 20;
            
            if (exportOptions.includeHeader) {
                pdf.setFontSize(12);
                pdf.setTextColor(100, 100, 100);
                pdf.text("BOULEVARD SÉCURITÉ", pdfWidth / 2, 10, { align: "center" });
                pdf.setFontSize(10);
                pdf.text(`Document généré le ${new Date().toLocaleDateString()}`, pdfWidth / 2, 15, { align: "center" });
            }
            
            pdf.addImage(imgData, 'JPEG', imgX, imgY, imgWidth * ratio, imgHeight * ratio);
            
            if (exportOptions.includeFooter) {
                const pageCount = pdf.internal.getNumberOfPages();
                for (let i = 1; i <= pageCount; i++) {
                    pdf.setPage(i);
                    pdf.setFontSize(10);
                    pdf.setTextColor(150, 150, 150);
                    pdf.text(
                        `Page ${i} / ${pageCount} - Contrat Réf. ${contrat?.referenceContrat || id}`,
                        pdfWidth / 2,
                        pdfHeight - 10,
                        { align: "center" }
                    );
                }
            }
            
            pdf.save(fileName);
            
            document.querySelectorAll('.articles-section, .fiches-paie-section').forEach(el => {
                el.style.display = '';
            });
            
            setExportInProgress(false);
            setShowExportLoader(false);
        }).catch(err => {
            console.error("Erreur lors de l'export PDF:", err);
            
            document.querySelectorAll('.articles-section, .fiches-paie-section').forEach(el => {
                el.style.display = '';
            });
            
            setExportInProgress(false);
            setShowExportLoader(false);
            alert("Une erreur s'est produite lors de l'export en PDF.");
        });
    };

    useEffect(() => {

        console.log("Chargement des détails du contrat avec ID:", id);
        setLoading(true);

        ContratDeTravailService.getById(id)
            .then(res => {
                const contratData = res.data;
                setContrat(contratData);
                console.log("Contrat chargé:", contratData);

                return Promise.all([
                    contratData.agentDeSecuriteId ?
                        AgentService.getAgentById(contratData.agentDeSecuriteId) :
                        Promise.resolve({ data: null }),

                    contratData.entrepriseId ?
                        EntrepriseService.getEntrepriseById(contratData.entrepriseId) :
                        Promise.resolve({ data: null }),

                    ArticleContratTravailService.getByContratTravail(Number(id)),

                    FicheDePaieService.getAll()
                ]);
            })
            .then(([agentRes, entrepriseRes, articlesRes, fichesRes]) => {
                setAgent(agentRes.data);
                setEntreprise(entrepriseRes.data);

                console.log("Articles récupérés (brut):", articlesRes);
                console.log("Articles récupérés (données):", articlesRes.data);
                const articlesData = Array.isArray(articlesRes.data) ? articlesRes.data : [];
                const articlesTries = articlesData.sort((a, b) => a.id - b.id);
                console.log("Articles après traitement et tri:", articlesTries);
                setArticles(articlesTries);

                console.log("Fiches de paie récupérées:", fichesRes.data);

                const contratId = Number(id);
                const fichesDuContrat = Array.isArray(fichesRes.data)
                    ? fichesRes.data.filter(fiche => {
                        const ficheContratId = Number(fiche.contratDeTravailId);
                        console.log(`Fiche ${fiche.id}: contrat=${ficheContratId}, comparé avec ${contratId}, égal: ${ficheContratId === contratId}`);
                        return ficheContratId === contratId;
                    })
                    : [];
                console.log("Fiches de paie filtrées:", fichesDuContrat);
                setFichesDePaie(fichesDuContrat);
            })
            .catch(err => {
                console.error("Erreur lors du chargement des données:", err);
                setError("Impossible de charger les détails du contrat.");
            })
            .finally(() => {
                setLoading(false);
            });

        ArticleContratTravailService.getByContratTravail(id)
            .then(refreshResponse => {
                console.log("Articles rafraîchis:", refreshResponse.data);
                setArticles(refreshResponse.data || []);
            })
            .catch(refreshError => {
                console.error("Erreur lors du rafraîchissement des articles:", refreshError);
            });

    }, [id]);

    if (error) return <p className="error">{error}</p>;
    if (loading) return <p className="loading">Chargement des données du contrat...</p>;
    if (!contrat) return <p>Contrat non trouvé.</p>;

    const formatDate = (dateString) => {
        if (!dateString) return "–";
        return dateString.slice(0, 10).split('-').reverse().join('/');
    };
    const handleViewPdf = () => {
        if (contrat.documentPdf) {
            setShowPdfPreview(!showPdfPreview);
        } else {
            alert("Aucun document PDF disponible pour ce contrat.");
        }
    };
    
    return (
        <div className="contrat-detail modern">
            {}
            {showExportLoader && (
                <div className="export-loader">
                    <div className="export-loader-spinner"></div>
                    <div className="export-loader-text">Génération du PDF en cours...</div>
                </div>
            )}
            
            <div className="contrat-header">
                <h2 className="title">📄 Contrat de Travail: {contrat.referenceContrat}</h2>
                
                <div className="pdf-action-buttons">
                    <button 
                        className="pdf-action-button options"
                        onClick={toggleExportOptions}
                        title="Options d'impression et d'export"
                    >
                        <span className="button-icon">⚙️</span> Options
                    </button>
                    
                    <button 
                        className="pdf-action-button print" 
                        onClick={handlePrint}
                        disabled={exportInProgress}
                        title="Imprimer le contrat"
                    >
                        <span className="button-icon">🖨️</span> Imprimer
                    </button>
                      
                    <button 
                        className="pdf-action-button export" 
                        onClick={exportToPDF}
                        disabled={exportInProgress}
                        title="Exporter en PDF"
                    >
                        <span className="button-icon">📥</span> Exporter en PDF
                        {exportInProgress && <span className="spinner"></span>}
                    </button>
                    
                    {contrat.documentPdf && (
                        <button
                            className="pdf-action-button download"
                            onClick={handleViewPdf}
                        >
                            <span className="button-icon">📑</span> {showPdfPreview ? 'Masquer le PDF' : 'PDF Original'}
                        </button>
                    )}
                </div>
            </div>
            
            {}
            {showExportOptions && (
                <div className="export-options not-printable">
                    <div className="options-title">Options d'impression et d'export PDF</div>
                    <div className="options-grid">
                        <div className="option-item">
                            <input 
                                type="checkbox" 
                                id="includeArticles" 
                                name="includeArticles"
                                checked={exportOptions.includeArticles}
                                onChange={handleExportOptionChange}
                            />
                            <label htmlFor="includeArticles">Inclure les articles</label>
                        </div>
                        <div className="option-item">
                            <input 
                                type="checkbox" 
                                id="includeFichesDePaie" 
                                name="includeFichesDePaie"
                                checked={exportOptions.includeFichesDePaie}
                                onChange={handleExportOptionChange}
                            />
                            <label htmlFor="includeFichesDePaie">Inclure les fiches de paie</label>
                        </div>
                        <div className="option-item">
                            <input 
                                type="checkbox" 
                                id="includeHeader" 
                                name="includeHeader"
                                checked={exportOptions.includeHeader}
                                onChange={handleExportOptionChange}
                            />
                            <label htmlFor="includeHeader">Inclure l'en-tête</label>
                        </div>
                        <div className="option-item">
                            <input 
                                type="checkbox" 
                                id="includeFooter" 
                                name="includeFooter"
                                checked={exportOptions.includeFooter}
                                onChange={handleExportOptionChange}
                            />
                            <label htmlFor="includeFooter">Inclure le pied de page</label>
                        </div>
                    </div>
                    <div className="layout-selector">
                        <label htmlFor="pageLayout">Orientation de la page: </label>
                        <select 
                            id="pageLayout"
                            name="pageLayout"
                            value={exportOptions.pageLayout}
                            onChange={handleExportOptionChange}
                        >
                            <option value="portrait">Portrait</option>
                            <option value="landscape">Paysage</option>
                        </select>
                    </div>
                </div>
            )}
            
            <div className="contrat-status">
                <span className={`status-badge ${contrat.dateFin ? (new Date(contrat.dateFin) < new Date() ? 'expired' : 'active') : 'active'}`}>
                    {contrat.dateFin ? (new Date(contrat.dateFin) < new Date() ? 'Expiré' : 'Actif') : 'En cours'}
                </span>
            </div>
            
            {}
            {showPdfPreview && contrat.documentPdf && (
                <div className="pdf-preview-container">
                    <h3>Document PDF Original</h3>
                    <div className="pdf-viewer">
                        <iframe
                            src={`data:application/pdf;base64,${contrat.documentPdf}`}
                            title={`Contrat de travail ${contrat.referenceContrat}`}
                            width="100%"
                            height="500px"
                        />
                    </div>
                    <div className="pdf-download">
                        <a
                            href={`data:application/pdf;base64,${contrat.documentPdf}`}
                            download={`contrat_${contrat.referenceContrat}.pdf`}
                            className="btn download-btn"
                        >
                            ⬇️ Télécharger le PDF original
                        </a>
                    </div>
                </div>
            )}
              {}
            <div className="printable-content" ref={targetRef}>
                {}
                <div className="print-header">
                    <div className="print-title">CONTRAT DE TRAVAIL</div>
                    <div className="print-subtitle">Référence: {contrat.referenceContrat}</div>
                </div>
                
                <div className="print-date">
                    Document généré le {new Date().toLocaleDateString()} à {new Date().toLocaleTimeString()}
                </div>            {}
            <div className="info-section" style={{ 
                backgroundColor: '#fff',
                borderRadius: '8px',
                boxShadow: '0 2px 10px rgba(0,0,0,0.05)',
                marginBottom: '30px',
                border: '1px solid #eaedf2'
            }}>
                <h3 style={{
                    borderBottom: '2px solid #eaedf2',
                    paddingBottom: '12px',
                    paddingLeft: '20px',
                    paddingTop: '20px',
                    marginTop: '0',
                    marginBottom: '20px',
                    color: '#2c3e50',
                    fontSize: '1.3rem',
                    fontWeight: '600',
                    backgroundColor: '#f8fafc',
                    borderTopLeftRadius: '8px',
                    borderTopRightRadius: '8px'
                }}>
                    <span style={{ marginRight: '10px' }}>📋</span>
                    Informations générales du contrat
                </h3>
                
                <div className="info-block" style={{ 
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))',
                    gap: '20px',
                    padding: '0 20px 20px'
                }}>
                    <div className="info-card" style={{
                        padding: '15px',
                        backgroundColor: '#f0f8ff',
                        borderRadius: '6px',
                        borderLeft: '4px solid #3498db'
                    }}>
                        <div style={{ fontSize: '0.9em', color: '#718096', marginBottom: '4px' }}>Type de contrat</div>
                        <div style={{ fontWeight: '600', fontSize: '1.1em' }}>{contrat.typeContrat || "–"}</div>
                    </div>
                    
                    <div className="info-card" style={{
                        padding: '15px',
                        backgroundColor: '#f0fff4',
                        borderRadius: '6px',
                        borderLeft: '4px solid #2ecc71'
                    }}>
                        <div style={{ fontSize: '0.9em', color: '#718096', marginBottom: '4px' }}>Date de début</div>
                        <div style={{ fontWeight: '600', fontSize: '1.1em' }}>{formatDate(contrat.dateDebut)}</div>
                    </div>
                    
                    <div className="info-card" style={{
                        padding: '15px',
                        backgroundColor: '#fff0f6',
                        borderRadius: '6px',
                        borderLeft: '4px solid #e84393'
                    }}>
                        <div style={{ fontSize: '0.9em', color: '#718096', marginBottom: '4px' }}>Date de fin</div>
                        <div style={{ fontWeight: '600', fontSize: '1.1em' }}>{formatDate(contrat.dateFin)}</div>
                    </div>
                    
                    <div className="info-card" style={{
                        padding: '15px',
                        backgroundColor: '#fffaf0',
                        borderRadius: '6px',
                        borderLeft: '4px solid #f39c12'
                    }}>
                        <div style={{ fontSize: '0.9em', color: '#718096', marginBottom: '4px' }}>Salaire</div>
                        <div style={{ fontWeight: '600', fontSize: '1.1em' }}>
                            {contrat.salaireDeBase} € 
                            <span style={{ fontSize: '0.8em', color: '#718096', marginLeft: '5px' }}>
                                ({contrat.periodiciteSalaire})
                            </span>
                        </div>
                    </div>
                    
                    <div className="info-card" style={{
                        padding: '15px',
                        backgroundColor: '#f7fafc',
                        borderRadius: '6px',
                        borderLeft: '4px solid #7f8c8d'
                    }}>
                        <div style={{ fontSize: '0.9em', color: '#718096', marginBottom: '4px' }}>Créé le</div>
                        <div style={{ fontWeight: '500', fontSize: '0.9em' }}>
                            {contrat.createdAt ? contrat.createdAt.replace("T"," ").slice(0,19) : "–"}
                        </div>
                    </div>
                    
                    <div className="info-card" style={{
                        padding: '15px',
                        backgroundColor: '#f7fafc',
                        borderRadius: '6px',
                        borderLeft: '4px solid #7f8c8d'
                    }}>
                        <div style={{ fontSize: '0.9em', color: '#718096', marginBottom: '4px' }}>Dernière mise à jour</div>
                        <div style={{ fontWeight: '500', fontSize: '0.9em' }}>
                            {contrat.updatedAt ? contrat.updatedAt.replace("T"," ").slice(0,19) : "–"}
                        </div>
                    </div>
                </div>
            </div>{}
            <div className="info-section agent-highlight">
                <h3>Agent de sécurité</h3>
                <div className="info-block agent-info-block">
                    {agent ? (
                        <>
                            <div className="agent-primary-info">
                                <div className="agent-avatar">
                                    {agent.photo ? 
                                        <img src={agent.photo} alt={`${agent.nom} ${agent.prenom}`} /> : 
                                        <div className="avatar-placeholder">
                                            {agent.nom && agent.prenom ? 
                                                `${agent.nom.charAt(0)}${agent.prenom.charAt(0)}` : 
                                                "AS"}
                                        </div>
                                    }
                                </div>
                                <div className="agent-main-details">
                                    <h4 className="agent-name">{agent.nom} {agent.prenom}</h4>
                                    <div className="agent-badge">Matricule: {agent.numeroMatricule}</div>
                                    <p className="agent-position">{agent.fonction || "Agent de sécurité"}</p>
                                </div>
                            </div>
                            
                            <div className="agent-secondary-info">
                                <div className="info-grid">
                                    <div className="info-item">
                                        <p className="info-label">Contact</p>
                                        <p className="info-value">{agent.telephone || "Non renseigné"}</p>
                                    </div>
                                    <div className="info-item">
                                        <p className="info-label">Email</p>
                                        <p className="info-value">{agent.email || "Non renseigné"}</p>
                                    </div>
                                    {agent.dateNaissance && (
                                        <div className="info-item">
                                            <p className="info-label">Date de naissance</p>
                                            <p className="info-value">{formatDate(agent.dateNaissance)}</p>
                                        </div>
                                    )}
                                    {agent.adresse && (
                                        <div className="info-item">
                                            <p className="info-label">Adresse</p>
                                            <p className="info-value">{agent.adresse}</p>
                                        </div>
                                    )}
                                </div>
                                
                                <div className="agent-action">
                                    <Link to={`/agents/${agent.id}`} className="detail-link">
                                        Voir le profil complet de l'agent
                                    </Link>
                                </div>
                            </div>
                        </>
                    ) : (
                        <p>Aucune information d'agent disponible.</p>
                    )}
                </div>
            </div>            {}
            <div className="info-section" style={{
                backgroundColor: '#fff',
                borderRadius: '8px',
                boxShadow: '0 2px 10px rgba(0,0,0,0.05)',
                marginBottom: '30px',
                border: '1px solid #eaedf2'
            }}>
                <h3 style={{
                    borderBottom: '2px solid #eaedf2',
                    paddingBottom: '12px',
                    paddingLeft: '20px',
                    paddingTop: '20px',
                    marginTop: '0',
                    marginBottom: '20px',
                    color: '#2c3e50',
                    fontSize: '1.3rem',
                    fontWeight: '600',
                    backgroundColor: '#f8fafc',
                    borderTopLeftRadius: '8px',
                    borderTopRightRadius: '8px'
                }}>
                    <span style={{ marginRight: '10px' }}>🏢</span>
                    Entreprise
                </h3>
                
                <div className="info-block" style={{ padding: '0 20px 20px' }}>
                    {entreprise ? (
                        <>
                            <div className="entreprise-card" style={{
                                display: 'flex',
                                flexDirection: 'column',
                                backgroundColor: '#f0f7ff',
                                borderRadius: '8px',
                                padding: '20px',
                                border: '1px solid #e3eaf2'
                            }}>
                                <div className="entreprise-header" style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    marginBottom: '15px'
                                }}>
                                    <div className="entreprise-logo" style={{
                                        width: '60px',
                                        height: '60px',
                                        borderRadius: '8px',
                                        backgroundColor: '#3498db',
                                        color: '#fff',
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: 'center',
                                        fontSize: '24px',
                                        fontWeight: 'bold',
                                        marginRight: '15px'
                                    }}>
                                        {entreprise.raisonSociale ? entreprise.raisonSociale.charAt(0) : 'E'}
                                    </div>
                                    <div>
                                        <h4 style={{
                                            margin: '0 0 5px 0',
                                            fontSize: '1.2rem',
                                            color: '#2c3e50'
                                        }}>
                                            {entreprise.raisonSociale}
                                        </h4>
                                        <p style={{
                                            margin: '0',
                                            color: '#718096',
                                            fontSize: '0.9rem'
                                        }}>
                                            SIRET: {entreprise.siret}
                                        </p>
                                    </div>
                                </div>
                                
                                <div className="entreprise-details" style={{
                                    display: 'grid',
                                    gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
                                    gap: '15px',
                                    marginBottom: '20px'
                                }}>
                                    <div className="detail-item">
                                        <p className="detail-label" style={{
                                            margin: '0 0 5px 0',
                                            fontSize: '0.8rem',
                                            color: '#718096'
                                        }}>Adresse</p>
                                        <p className="detail-value" style={{
                                            margin: '0',
                                            fontWeight: '500',
                                            color: '#2d3748'
                                        }}>{entreprise.adresse || 'Non renseignée'}</p>
                                    </div>
                                    
                                    <div className="detail-item">
                                        <p className="detail-label" style={{
                                            margin: '0 0 5px 0',
                                            fontSize: '0.8rem',
                                            color: '#718096'
                                        }}>Contact</p>
                                        <p className="detail-value" style={{
                                            margin: '0',
                                            fontWeight: '500',
                                            color: '#2d3748'
                                        }}>{entreprise.telephone || 'Non renseigné'}</p>
                                    </div>
                                    
                                    {entreprise.email && (
                                        <div className="detail-item">
                                            <p className="detail-label" style={{
                                                margin: '0 0 5px 0',
                                                fontSize: '0.8rem',
                                                color: '#718096'
                                            }}>Email</p>
                                            <p className="detail-value" style={{
                                                margin: '0',
                                                fontWeight: '500',
                                                color: '#2d3748'
                                            }}>{entreprise.email}</p>
                                        </div>
                                    )}
                                </div>
                                
                                <div style={{ textAlign: 'right' }}>
                                    <Link to={`/entreprises/${entreprise.id}`} className="detail-link" style={{
                                        color: '#3498db',
                                        textDecoration: 'none',
                                        fontWeight: '500',
                                        display: 'inline-flex',
                                        alignItems: 'center'
                                    }}>
                                        Voir le profil complet de l'entreprise
                                        <span style={{ marginLeft: '5px' }}>→</span>
                                    </Link>
                                </div>
                            </div>
                        </>
                    ) : (
                        <div style={{
                            padding: '25px',
                            backgroundColor: '#f8f9fa',
                            borderRadius: '8px',
                            textAlign: 'center',
                            border: '1px dashed #cbd5e0'
                        }}>
                            <p style={{ margin: '0', color: '#718096' }}>
                                Aucune information d'entreprise disponible.
                            </p>
                        </div>
                    )}
                </div>
            </div>

            {}
            {contrat.description && (
                <div className="info-section" style={{
                    backgroundColor: '#fff',
                    borderRadius: '8px',
                    boxShadow: '0 2px 10px rgba(0,0,0,0.05)',
                    marginBottom: '30px',
                    border: '1px solid #eaedf2'
                }}>
                    <h3 style={{
                        borderBottom: '2px solid #eaedf2',
                        paddingBottom: '12px',
                        paddingLeft: '20px',
                        paddingTop: '20px',
                        marginTop: '0',
                        marginBottom: '20px',
                        color: '#2c3e50',
                        fontSize: '1.3rem',
                        fontWeight: '600',
                        backgroundColor: '#f8fafc',
                        borderTopLeftRadius: '8px',
                        borderTopRightRadius: '8px'
                    }}>
                        <span style={{ marginRight: '10px' }}>📝</span>
                        Description
                    </h3>
                    <div className="info-block" style={{ padding: '0 20px 20px' }}>
                        <div style={{
                            backgroundColor: '#f8f9fa',
                            padding: '20px',
                            borderRadius: '6px',
                            lineHeight: '1.6',
                            whiteSpace: 'pre-wrap'
                        }}>
                            {contrat.description}
                        </div>
                    </div>
                </div>
            )}

            {}            <div className="info-section articles-section">
                <div className="section-header" style={{ marginBottom: '20px' }}>
                    <h3>Articles du contrat ({articles.length})</h3>
                </div>

                {}                {articles.length > 0 ? (
                    <div className="articles-list">
                        {articles.map(article => (
                            <div key={article.id} className="article-item" style={{
                                border: '1px solid #ddd',
                                borderRadius: '8px',
                                margin: '15px 0',
                                padding: '20px',
                                backgroundColor: '#fff',
                                boxShadow: '0 2px 4px rgba(0,0,0,0.05)'
                            }}>
                                <div className="article-header">
                                    <h4 className="article-title" style={{
                                        fontWeight: '600',
                                        marginBottom: '15px',
                                        color: '#2c3e50',
                                        borderBottom: '2px solid #ecf0f1',
                                        paddingBottom: '10px'
                                    }}>
                                        {article.libelle || 'Article sans titre'}
                                    </h4>
                                </div>

                                {}
                                <div className="article-content" style={{
                                    padding: '15px',
                                    backgroundColor: '#f8f9fa',
                                    border: '1px solid #eee',
                                    borderRadius: '6px'
                                }}>
                                    <div className="article-metadata" style={{
                                        display: 'flex', 
                                        flexWrap: 'wrap', 
                                        gap: '20px', 
                                        marginBottom: '15px',
                                        background: '#edf2f7',
                                        padding: '10px',
                                        borderRadius: '4px'
                                    }}>
                                        <p style={{margin: '0'}}><strong>ID :</strong> {article.id}</p>
                                        {article.createdAt && <p style={{margin: '0'}}><strong>Créé le :</strong> {new Date(article.createdAt).toLocaleString()}</p>}
                                        {article.updatedAt && <p style={{margin: '0'}}><strong>Mis à jour le :</strong> {new Date(article.updatedAt).toLocaleString()}</p>}
                                    </div>

                                    <div className="article-text" style={{
                                        whiteSpace: 'pre-wrap',
                                        padding: '20px',
                                        border: '1px solid #e2e8f0',
                                        borderRadius: '6px',
                                        backgroundColor: '#fff',
                                        marginBottom: '15px',
                                        fontFamily: "'Segoe UI', Arial, sans-serif",
                                        lineHeight: '1.6',
                                        boxShadow: 'inset 0 2px 4px rgba(0,0,0,0.03)'
                                    }}>
                                        <h5 style={{marginTop: 0, color: '#3498db', fontWeight: '600', marginBottom: '12px'}}>
                                            Contenu de l'article :
                                        </h5>
                                        {article.contenu || 'Aucun contenu disponible.'}
                                    </div>

                                    <div className="article-meta" style={{textAlign: 'right'}}>
                                        <p className="article-relation-info" style={{
                                            fontSize: '0.9em', 
                                            color: '#718096',
                                            fontStyle: 'italic',
                                            marginBottom: '0'
                                        }}>
                                            Article n°{article.id} du contrat de travail #{article.contratDeTravailId}
                                        </p>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                ) : (
                    <div className="info-block empty-state" style={{
                        padding: '20px',
                        backgroundColor: '#f8f9fa',
                        borderRadius: '4px',
                        textAlign: 'center'
                    }}>
                        <p style={{ margin: '10px 0', color: '#6c757d' }}>
                            <span role="img" aria-label="information" style={{ fontSize: '24px', marginRight: '10px' }}>ℹ️</span>
                            Aucun article associé à ce contrat.
                        </p>
                    </div>
                )}
            </div>            {}
            <div className="info-section fiches-paie-section">
                <h3 style={{
                    color: '#2c3e50',
                    borderBottom: '2px solid #ecf0f1',
                    paddingBottom: '10px',
                    marginBottom: '20px'
                }}>Fiches de paie ({fichesDePaie.length})</h3>
                
                {fichesDePaie.length > 0 ? (
                    <div className="table-wrapper" style={{
                        borderRadius: '8px',
                        overflow: 'hidden',
                        boxShadow: '0 4px 8px rgba(0,0,0,0.05)'
                    }}>
                        <table style={{
                            width: '100%',
                            borderCollapse: 'collapse',
                            backgroundColor: '#fff',
                            fontSize: '14px'
                        }}>
                            <thead>
                                <tr style={{
                                    backgroundColor: '#3498db',
                                    color: 'white',
                                    textAlign: 'left'
                                }}>
                                    <th style={{ padding: '12px 15px' }}>Référence</th>
                                    <th style={{ padding: '12px 15px' }}>Période</th>
                                    <th style={{ padding: '12px 15px' }}>Net à payer</th>
                                    <th style={{ padding: '12px 15px' }}>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {fichesDePaie.map((fiche, index) => (
                                    <tr key={fiche.id} style={{
                                        borderBottom: '1px solid #ecf0f1',
                                        backgroundColor: index % 2 === 0 ? '#f8f9fa' : '#fff'
                                    }}>
                                        <td style={{ padding: '12px 15px', fontWeight: '500' }}>{fiche.reference}</td>
                                        <td style={{ padding: '12px 15px' }}>
                                            <div style={{ display: 'flex', alignItems: 'center' }}>
                                                <span className="date-badge" style={{
                                                    backgroundColor: '#e3f2fd',
                                                    color: '#1976d2',
                                                    padding: '4px 8px',
                                                    borderRadius: '4px',
                                                    fontSize: '12px',
                                                    marginRight: '8px'
                                                }}>
                                                    {formatDate(fiche.periodeDebut)}
                                                </span>
                                                <span style={{ margin: '0 5px' }}>→</span>
                                                <span className="date-badge" style={{
                                                    backgroundColor: '#e3f2fd',
                                                    color: '#1976d2',
                                                    padding: '4px 8px',
                                                    borderRadius: '4px',
                                                    fontSize: '12px'
                                                }}>
                                                    {formatDate(fiche.periodeFin)}
                                                </span>
                                            </div>
                                        </td>
                                        <td style={{ 
                                            padding: '12px 15px', 
                                            fontWeight: '600',
                                            color: '#2ecc71' 
                                        }}>
                                            {fiche.netAPayer} €
                                        </td>
                                        <td style={{ padding: '12px 15px' }}>
                                            <Link to={`/fiches-de-paie/${fiche.id}`} className="btn view" style={{
                                                backgroundColor: '#3498db',
                                                color: 'white',
                                                padding: '6px 12px',
                                                borderRadius: '4px',
                                                textDecoration: 'none',
                                                fontSize: '13px',
                                                display: 'inline-flex',
                                                alignItems: 'center',
                                                justifyContent: 'center',
                                                border: 'none',
                                                cursor: 'pointer',
                                                transition: 'background-color 0.2s'
                                            }}>
                                                <span style={{ marginRight: '5px' }}>👁️</span> Consulter
                                            </Link>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                ) : (
                    <div className="info-block empty-state" style={{
                        padding: '25px',
                        backgroundColor: '#f8f9fa',
                        borderRadius: '8px',
                        textAlign: 'center',
                        border: '1px dashed #cbd5e0'
                    }}>
                        <div style={{ marginBottom: '10px' }}>
                            <span role="img" aria-label="information" style={{ 
                                fontSize: '32px', 
                                display: 'block',
                                marginBottom: '10px'
                            }}>📋</span>
                        </div>
                        <p style={{ 
                            margin: '5px 0 15px', 
                            color: '#4a5568',
                            fontSize: '16px',
                            fontWeight: '500'
                        }}>
                            Aucune fiche de paie n'est associée à ce contrat.
                        </p>
                        <p style={{ 
                            margin: '0', 
                            color: '#718096',
                            fontSize: '14px'
                        }}>
                            Les fiches de paie apparaîtront ici une fois créées.
                        </p>
                    </div>
                )}
            </div>            {}
            <div className="actions-container">
                <Link to="/contrats-de-travail" className="back-link">⬅ Retour à la liste</Link>
                <Link to={`/contrats-de-travail/edit/${id}`} className="edit-link">✏️ Modifier ce contrat</Link>
            </div>
            </div> {}
        </div>
    );
};

export default ContratDeTravailDetail;
