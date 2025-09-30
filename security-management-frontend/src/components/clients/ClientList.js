// src/components/clients/ClientList.jsx
import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { 
  Container, Row, Col, Card, Table, Button, Form, 
  InputGroup, Pagination, Badge, Spinner, Alert, Dropdown
} from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { 
  faUserTie, faSearch, faPlus, faEdit, faTrashAlt, 
  faEye, faSort, faSortUp, faSortDown, faFilter, faSync, faCheckCircle,
  faEllipsisV, faUserShield, faUserTag, faUserGear
} from "@fortawesome/free-solid-svg-icons";
import ClientService from "../../services/ClientService";
import adminAccountsService from "../../services/adminAccountsService";
import { useAuth } from "../../context/AuthContext";
import "../../styles/ClientList.css";

export default function ClientList() {
  /* ─── context ──────────────────────────────────────────── */
  const { user } = useAuth();
  const isAdmin = user && user.role === 'ADMIN';

  /* ─── state ────────────────────────────────────────────── */
  const [clients, setClients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [totalItems, setTotalItems] = useState(0);
  
  // Filtres et tri
  const [searchTerm, setSearchTerm] = useState('');
  const [sortField, setSortField] = useState('id');
  const [sortDirection, setSortDirection] = useState('asc');
  const [filterType, setFilterType] = useState('');
  const [filterStatus, setFilterStatus] = useState(''); // Nouveau filtre pour le statut
  const [clientTypes, setClientTypes] = useState([]);
  const [pendingCount, setPendingCount] = useState(0); // Compteur des comptes en attente

  /* ─── fetch data with pagination ───────────────────────── */
  useEffect(() => {
    let mounted = true;                         // évite le setState après un unmount
    setLoading(true);
      // Dans un environnement réel, l'API devrait supporter la pagination côté serveur
    // Ici, nous simulons avec une gestion côté client
    ClientService.getAll()
      .then(response => {
        const allClients = response.data;
        if (mounted) {
          // Extraire les types de clients uniques pour le filtre
          const types = new Set();
          console.log("Clients reçus:", allClients);
          allClients.forEach(client => {
            console.log("Client:", client);
            if (client.typeClient) {
              types.add(client.typeClient);
            }
          });
          setClientTypes(Array.from(types));
          
          // Calculer le nombre de comptes en attente
          const pendingClients = allClients.filter(client => 
            client.accountValidated === false || client.accountValidated === undefined
          );
          setPendingCount(pendingClients.length);
          
          // Appliquer les filtres et le tri
          let filteredClients = applyFiltersAndSort(allClients);
          setTotalItems(filteredClients.length);
          // Pagination
          const indexOfLastItem = currentPage * itemsPerPage;
          const indexOfFirstItem = indexOfLastItem - itemsPerPage;
          const currentItems = filteredClients.slice(indexOfFirstItem, indexOfLastItem);
          setClients(currentItems);
          setLoading(false);
        }
      })
      .catch(err => {
        console.error("Erreur lors du chargement des clients:", err);
        if (mounted) {
          setError("Une erreur s'est produite lors du chargement des clients.");
          setLoading(false);
        }
      });

    return () => (mounted = false);             // clean up
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage, itemsPerPage, searchTerm, sortField, sortDirection, filterType, filterStatus]);
  // Fonction pour normaliser les données des clients
  const normalizeClientData = (client) => {
    return {
      ...client,
      // Ajouter des valeurs par défaut pour les champs qui pourraient être undefined
      id: client.id,
      nom: client.nom || '',
      prenom: client.prenom || '',
      email: client.email || '',
      telephone: client.telephone || '',
      adresse: client.adresse || '',
      numeroRue: client.numeroRue || '',
      codePostal: client.codePostal || '',
      ville: client.ville || '',
      pays: client.pays || 'France',
      role: client.role || 'CLIENT',
      typeClient: client.typeClient || 'CLIENT',
      siege: client.siege || '',
      representant: client.representant || '',
      numeroSiret: client.numeroSiret || '',
      modeContactPrefere: client.modeContactPrefere || '',
      username: client.username || '',
      // Correction: utiliser le bon champ du backend
      accountValidated: client.adminApproved === true,
      emailVerified: client.emailVerified === true
    };
  };

  // Fonction utilitaire pour appliquer les filtres et le tri
  const applyFiltersAndSort = (clientsList) => {
    // Normaliser les données avant de les utiliser
    let result = clientsList.map(client => normalizeClientData(client));
    
    // Filtrage par terme de recherche
    if (searchTerm) {
      result = result.filter(client => 
        client.nom.toLowerCase().includes(searchTerm.toLowerCase()) ||
        client.prenom.toLowerCase().includes(searchTerm.toLowerCase()) ||
        client.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
        client.telephone.includes(searchTerm) ||
        client.username.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }
    
    // Filtrage par type
    if (filterType) {
      result = result.filter(client => client.typeClient === filterType);
    }
    
    // Filtrage par statut de validation
    if (filterStatus) {
      if (filterStatus === 'validated') {
        result = result.filter(client => client.accountValidated === true);
      } else if (filterStatus === 'pending') {
        result = result.filter(client => client.accountValidated === false);
      }
    }
    
    // Tri
    result.sort((a, b) => {
      const aValue = a[sortField] || '';
      const bValue = b[sortField] || '';
      
      if (typeof aValue === 'string') {
        return sortDirection === 'asc' 
          ? aValue.localeCompare(bValue) 
          : bValue.localeCompare(aValue);
      } else {
        return sortDirection === 'asc' 
          ? aValue - bValue 
          : bValue - aValue;
      }
    });
    
    return result;
  };

  /* ─── delete one client ───────────────────────────────── */
  const handleDelete = async id => {
    if (!window.confirm("Êtes-vous sûr de vouloir supprimer ce client ?")) return;
    try {
      setLoading(true);
      await ClientService.delete(id);
        // Rafraîchir la liste après suppression
      ClientService.getAll()
        .then(allClients => {
                    // Re-appliquer les filtres et tri
          let filteredClients = applyFiltersAndSort(allClients);
          
          // Calculer le nombre de comptes en attente
          const pendingClients = allClients.filter(client => 
            client.accountValidated === false || client.accountValidated === undefined
          );
          setPendingCount(pendingClients.length);
          
          // Ajuster la page courante si nécessaire
          const maxPage = Math.ceil(filteredClients.length / itemsPerPage);
          const newCurrentPage = currentPage > maxPage ? maxPage || 1 : currentPage;
          
          // Appliquer la pagination
          const indexOfLastItem = newCurrentPage * itemsPerPage;
          const indexOfFirstItem = indexOfLastItem - itemsPerPage;
          const currentItems = filteredClients.slice(indexOfFirstItem, indexOfLastItem);
          
          setClients(currentItems);
          setTotalItems(filteredClients.length);
          setCurrentPage(newCurrentPage);
          setLoading(false);
        })
        .catch(err => {
          console.error("Erreur lors du rafraîchissement des clients:", err);
          setError("Une erreur s'est produite lors de la mise à jour de la liste.");
          setLoading(false);
        });
    } catch (err) {
      console.error("Erreur lors de la suppression:", err);
      setError("Une erreur s'est produite lors de la suppression du client.");
      setLoading(false);
    }
  };
  
  /* ─── validate client account ──────────────────────────── */
  const handleValidateAccount = async (clientId, clientName) => {
    if (!window.confirm(`Êtes-vous sûr de vouloir valider le compte de ${clientName} ?`)) return;
    
    try {
      setLoading(true);
      await adminAccountsService.approveClient(clientId);
      
      // Rafraîchir la liste après validation
      ClientService.getAll()
        .then(response => {
          const allClients = response.data;
          if (allClients) {
            // Extraire les types de clients uniques pour le filtre
            const types = new Set();
            allClients.forEach(client => {
              if (client.typeClient) {
                types.add(client.typeClient);
              }
            });
            setClientTypes(Array.from(types));
            
            // Calculer le nombre de comptes en attente
            const pendingClients = allClients.filter(client => 
              client.accountValidated === false || client.accountValidated === undefined
            );
            setPendingCount(pendingClients.length);
            
            // Appliquer les filtres et le tri
            let filteredClients = applyFiltersAndSort(allClients);
            setTotalItems(filteredClients.length);
            
            // Pagination
            const indexOfLastItem = currentPage * itemsPerPage;
            const indexOfFirstItem = indexOfLastItem - itemsPerPage;
            const currentItems = filteredClients.slice(indexOfFirstItem, indexOfLastItem);
            
            setClients(currentItems);
            setLoading(false);
            
            // Message de succès (optionnel)
            alert(`Le compte de ${clientName} a été validé avec succès !`);
          }
        })
        .catch(err => {
          console.error("Erreur lors du rafraîchissement des clients:", err);
          setError("Une erreur s'est produite lors de la mise à jour de la liste.");
          setLoading(false);
        });
        
    } catch (err) {
      console.error("Erreur lors de la validation du compte:", err);
      setError("Une erreur s'est produite lors de la validation du compte client.");
      setLoading(false);
      alert("Erreur lors de la validation du compte. Veuillez réessayer.");
    }
  };
  
  /* ─── change client role ───────────────────────────────── */
  const handleChangeRole = async (clientId, clientName, newRole) => {
    if (!window.confirm(`Êtes-vous sûr de vouloir changer le rôle de ${clientName} vers ${newRole} ?`)) return;
    
    try {
      setLoading(true);
      console.log(`Tentative de changement de rôle pour le client ${clientId} vers ${newRole}`);
      
      // Utiliser le nouvel endpoint spécialisé pour éviter les problèmes de lazy loading
      console.log("Envoi de la mise à jour du rôle...");
      await adminAccountsService.changeClientRole(clientId, newRole);
      console.log("Mise à jour réussie !");
      
      // Rafraîchir la liste après modification
      const allClientsResponse = await ClientService.getAll();
      const allClients = allClientsResponse.data;
      
      if (allClients) {
        // Extraire les types de clients uniques pour le filtre
        const types = new Set();
        allClients.forEach(client => {
          if (client.typeClient) {
            types.add(client.typeClient);
          }
        });
        setClientTypes(Array.from(types));
        
        // Calculer le nombre de comptes en attente
        const pendingClients = allClients.filter(client => 
          client.adminApproved === false || client.adminApproved === undefined
        );
        setPendingCount(pendingClients.length);
        
        // Appliquer les filtres et le tri
        let filteredClients = applyFiltersAndSort(allClients);
        setTotalItems(filteredClients.length);
        
        // Pagination
        const indexOfLastItem = currentPage * itemsPerPage;
        const indexOfFirstItem = indexOfLastItem - itemsPerPage;
        const currentItems = filteredClients.slice(indexOfFirstItem, indexOfLastItem);
        
        setClients(currentItems);
        setLoading(false);
        
        alert(`Le rôle de ${clientName} a été changé vers ${newRole} avec succès !`);
      }
        
    } catch (err) {
      console.error("Erreur détaillée lors du changement de rôle:", err);
      console.error("Response data:", err.response?.data);
      console.error("Response status:", err.response?.status);
      console.error("Response headers:", err.response?.headers);
      
      // Message d'erreur plus spécifique selon le type d'erreur
      let errorMessage = "Une erreur s'est produite lors du changement de rôle.";
      if (err.response) {
        switch (err.response.status) {
          case 404:
            errorMessage = "Client non trouvé.";
            break;
          case 403:
            errorMessage = "Vous n'avez pas les droits pour effectuer cette action.";
            break;
          case 500:
            errorMessage = "Erreur serveur. Veuillez réessayer plus tard.";
            break;
          default:
            errorMessage = `Erreur ${err.response.status}: ${err.response.data?.message || 'Erreur inconnue'}`;
        }
      } else if (err.message) {
        errorMessage = err.message;
      }
      
      setError(errorMessage);
      setLoading(false);
      alert(`Erreur: ${errorMessage}`);
    }
  };
  
  // Gestion du changement de page
  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };
  
  // Gestion du changement d'éléments par page
  const handleItemsPerPageChange = (e) => {
    setItemsPerPage(Number(e.target.value));
    setCurrentPage(1); // Retour à la première page
  };
  
  // Gestion du tri
  const handleSort = (field) => {
    const newDirection = sortField === field && sortDirection === 'asc' ? 'desc' : 'asc';
    setSortField(field);
    setSortDirection(newDirection);
  };
    // Gestion de la recherche
  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value);
    setCurrentPage(1); // Retour à la première page
  };
  
  // Réinitialiser les filtres
  const handleResetFilters = () => {
    setSearchTerm('');
    setFilterType('');
    setFilterStatus('');
    setSortField('id');
    setSortDirection('asc');
    setCurrentPage(1);
  };
  
  // Afficher le console.log pour le débogage
  useEffect(() => {
    console.log("État actuel des clients:", clients);
  }, [clients]);
  
  // Fonction pour afficher l'icône de tri appropriée
  const renderSortIcon = (field) => {
    if (sortField !== field) {
      return <FontAwesomeIcon icon={faSort} className="ms-1 text-muted" />;
    }
    return sortDirection === 'asc' 
      ? <FontAwesomeIcon icon={faSortUp} className="ms-1 text-primary" />
      : <FontAwesomeIcon icon={faSortDown} className="ms-1 text-primary" />;
  };
  
  // Générer les items de pagination
  const renderPaginationItems = () => {
    const totalPages = Math.ceil(totalItems / itemsPerPage);
    let items = [];
    
    // Ajouter "Premier" et "Précédent"
    items.push(
      <Pagination.First 
        key="first" 
        onClick={() => handlePageChange(1)} 
        disabled={currentPage === 1} 
      />
    );
    items.push(
      <Pagination.Prev 
        key="prev" 
        onClick={() => handlePageChange(currentPage - 1)} 
        disabled={currentPage === 1} 
      />
    );
    
    // Limiter le nombre de pages affichées
    let startPage = Math.max(1, currentPage - 2);
    let endPage = Math.min(totalPages, startPage + 4);
    
    if (endPage - startPage < 4) {
      startPage = Math.max(1, endPage - 4);
    }
    
    // Première page
    if (startPage > 1) {
      items.push(
        <Pagination.Item key={1} onClick={() => handlePageChange(1)}>
          1
        </Pagination.Item>
      );
      if (startPage > 2) {
        items.push(<Pagination.Ellipsis key="ellipsis1" />);
      }
    }
    
    // Pages centrales
    for (let i = startPage; i <= endPage; i++) {
      items.push(
        <Pagination.Item 
          key={i} 
          active={currentPage === i}
          onClick={() => handlePageChange(i)}
        >
          {i}
        </Pagination.Item>
      );
    }
    
    // Dernière page
    if (endPage < totalPages) {
      if (endPage < totalPages - 1) {
        items.push(<Pagination.Ellipsis key="ellipsis2" />);
      }
      items.push(
        <Pagination.Item 
          key={totalPages} 
          onClick={() => handlePageChange(totalPages)}
        >
          {totalPages}
        </Pagination.Item>
      );
    }
    
    // Ajouter "Suivant" et "Dernier"
    items.push(
      <Pagination.Next 
        key="next" 
        onClick={() => handlePageChange(currentPage + 1)} 
        disabled={currentPage === totalPages} 
      />
    );
    items.push(
      <Pagination.Last 
        key="last" 
        onClick={() => handlePageChange(totalPages)} 
        disabled={currentPage === totalPages} 
      />
    );
    
    return items;
  };

  /* ─── UI states ───────────────────────────────────────── */
  if (loading) {
    return (
      <Container className="my-5 text-center">
        <Spinner animation="border" variant="primary" />
        <p className="mt-3">Chargement des données...</p>
      </Container>
    );
  }
  
  if (error) {
    return (
      <Container className="my-5">
        <Alert variant="danger">
          <Alert.Heading>Erreur</Alert.Heading>
          <p>{error}</p>
          <Button 
            variant="outline-danger" 
            onClick={() => window.location.reload()}>
            Réessayer
          </Button>
        </Alert>
      </Container>
    );
  }
  
  if (!clients.length && !searchTerm && !filterType) {
    return (
      <Container className="my-5">
        <Card className="shadow-sm">
          <Card.Body className="p-4">
            <Card.Title className="mb-4">
              <FontAwesomeIcon icon={faUserTie} className="me-2" />
              Liste des Clients
            </Card.Title>
            <Alert variant="info">
              <p className="mb-0">Aucun client pour l'instant.</p>
            </Alert>
            <div className="mt-3">
              <Button 
                as={Link} 
                to="/clients/create" 
                variant="primary"
              >
                <FontAwesomeIcon icon={faPlus} className="me-2" />
                Créer un nouveau client
              </Button>
            </div>
          </Card.Body>
        </Card>
      </Container>
    );
  }

  /* ─── render table ─────────────────────────────────────── */
  return (
    <Container fluid className="my-5">
      <Card className="shadow-sm border-0">
        <Card.Body className="p-4">
          <Card.Title className="d-flex justify-content-between align-items-center mb-4">
            <div className="d-flex align-items-center">
              <h4 className="mb-0 me-3">
                <FontAwesomeIcon icon={faUserTie} className="me-2" />
                Liste des Clients
              </h4>
              {isAdmin && pendingCount > 0 && (
                <Badge 
                  bg="warning" 
                  text="dark" 
                  className="fs-6 cursor-pointer" 
                  title="Cliquer pour voir uniquement les comptes en attente"
                  onClick={() => {
                    setFilterStatus('pending');
                    setCurrentPage(1);
                  }}
                >
                  {pendingCount} en attente de validation
                </Badge>
              )}
            </div>
            <div className="d-flex gap-2">
              {isAdmin && (
                <Button 
                  variant="outline-warning"
                  size="sm"
                  onClick={() => {
                    setFilterStatus('pending');
                    setCurrentPage(1);
                  }}
                  title="Voir seulement les comptes en attente"
                >
                  <FontAwesomeIcon icon={faFilter} className="me-1" />
                  En attente
                </Button>
              )}
              <Button 
                as={Link} 
                to="/clients/create" 
                variant="primary"
                className="d-flex align-items-center"
              >
                <FontAwesomeIcon icon={faPlus} className="me-2" />
                Nouveau client
              </Button>
            </div>
          </Card.Title>
          
          {/* Filtres et recherche */}
          <Row className="mb-4 g-3">
            <Col md={6} lg={4}>
              <InputGroup>
                <InputGroup.Text>
                  <FontAwesomeIcon icon={faSearch} />
                </InputGroup.Text>
                <Form.Control
                  placeholder="Rechercher un client..."
                  value={searchTerm}
                  onChange={handleSearchChange}
                />
                {searchTerm && (
                  <Button 
                    variant="outline-secondary" 
                    onClick={() => setSearchTerm('')}
                  >
                    &times;
                  </Button>
                )}
              </InputGroup>
            </Col>
            <Col md={3} lg={2}>
              <Form.Group>
                <Form.Select
                  value={filterType}
                  onChange={e => {
                    setFilterType(e.target.value);
                    setCurrentPage(1);
                  }}
                >
                  <option value="">Tous les types</option>
                  {clientTypes.map((type, index) => (
                    <option key={index} value={type}>{type}</option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            {isAdmin && (
              <Col md={3} lg={2}>
                <Form.Group>
                  <Form.Select
                    value={filterStatus}
                    onChange={e => {
                      setFilterStatus(e.target.value);
                      setCurrentPage(1);
                    }}
                  >
                    <option value="">Tous les statuts</option>
                    <option value="validated">Validés</option>
                    <option value="pending">En attente</option>
                  </Form.Select>
                </Form.Group>
              </Col>
            )}
            <Col md={3} lg={2}>
              <Form.Group>
                <Form.Select 
                  value={itemsPerPage} 
                  onChange={handleItemsPerPageChange}
                >
                  <option value={5}>5 par page</option>
                  <option value={10}>10 par page</option>
                  <option value={20}>20 par page</option>
                  <option value={50}>50 par page</option>
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={12} lg={2}>
              <Button 
                variant="outline-secondary" 
                className="w-100" 
                onClick={handleResetFilters}
              >
                <FontAwesomeIcon icon={faSync} className="me-2" />
                Réinitialiser
              </Button>
            </Col>
          </Row>
          
          {/* Message si aucun résultat avec les filtres */}
          {clients.length === 0 && (searchTerm || filterType || filterStatus) && (
            <Alert variant="info">
              Aucun client ne correspond à vos critères de recherche.
              <Button 
                variant="link" 
                className="p-0 ms-2" 
                onClick={handleResetFilters}
              >
                Effacer les filtres
              </Button>
            </Alert>
          )}
          
          {/* Affichage de la table */}
          {clients.length > 0 && (
            <>
              <div className="table-responsive">
                <Table hover className="table-striped align-middle">
                  <thead className="table-light">
                    <tr>
                      <th 
                        className="cursor-pointer" 
                        onClick={() => handleSort('id')}
                      >
                        ID {renderSortIcon('id')}
                      </th>
                      <th 
                        className="cursor-pointer" 
                        onClick={() => handleSort('username')}
                      >
                        Username {renderSortIcon('username')}
                      </th>
                      <th 
                        className="cursor-pointer" 
                        onClick={() => handleSort('role')}
                      >
                        Rôle {renderSortIcon('role')}
                      </th>
                      <th 
                        className="cursor-pointer" 
                        onClick={() => handleSort('typeClient')}
                      >
                        Type {renderSortIcon('typeClient')}
                      </th>
                      <th 
                        className="cursor-pointer" 
                        onClick={() => handleSort('email')}
                      >
                        Email {renderSortIcon('email')}
                      </th>
                      <th 
                        className="cursor-pointer" 
                        onClick={() => handleSort('nom')}
                      >
                        Nom {renderSortIcon('nom')}
                      </th>
                      <th 
                        className="cursor-pointer" 
                        onClick={() => handleSort('prenom')}
                      >
                        Prénom {renderSortIcon('prenom')}
                      </th>
                      <th 
                        className="cursor-pointer" 
                        onClick={() => handleSort('telephone')}
                      >
                        Tél. {renderSortIcon('telephone')}
                      </th>
                      <th>Statut</th>
                      <th>Adresse</th>
                      <th className="text-center">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {clients.map(client => (
                      <tr key={client.id}>
                        <td>{client.id}</td>
                        <td>{client.username || "—"}</td>
                        <td>
                          <Badge 
                            bg={
                              client.role === 'ADMIN' ? 'danger' : 
                              client.role === 'CLIENT' ? 'primary' : 
                              client.role === 'AGENT_SECURITE' ? 'success' : 'secondary'
                            }
                          >
                            {client.role === 'AGENT_SECURITE' ? 'AGENT SÉCURITÉ' : client.role || "—"}
                          </Badge>
                        </td>
                        <td>
                          <Badge 
                            bg={
                              client.typeClient === 'ENTREPRISE' ? 'success' : 
                              client.typeClient === 'PARTICULIER' ? 'info' : 'secondary'
                            }
                          >
                            {client.typeClient || "—"}
                          </Badge>
                        </td>
                        <td>{client.email || "—"}</td>
                        <td>{client.nom || "—"}</td>
                        <td>{client.prenom || "—"}</td>
                        <td>{client.telephone || "—"}</td>
                        <td className="status-cell">
                          {client.accountValidated ? (
                            <div className="status-validated">
                              <Badge bg="success">
                                <FontAwesomeIcon icon={faCheckCircle} className="me-1" />
                                Validé
                              </Badge>
                            </div>
                          ) : (
                            <div className="status-pending">
                              <div className="d-flex align-items-center gap-2">
                                <Badge bg="warning" text="dark">
                                  En attente validation admin
                                </Badge>
                              </div>
                            </div>
                          )}
                        </td>
                        <td>
                          {[client.adresse, client.codePostal, client.ville]
                            .filter(Boolean)
                            .join(" ") || "—"}
                        </td>
                        <td>
                          <div className="d-flex justify-content-center gap-2">
                            {/* Boutons d'action normaux */}
                            <Button 
                              as={Link} 
                              to={`/clients/${client.id}`}
                              variant="outline-primary"
                              size="sm"
                              title="Voir les détails"
                            >
                              <FontAwesomeIcon icon={faEye} />
                            </Button>
                            <Button 
                              as={Link} 
                              to={`/clients/edit/${client.id}`}
                              variant="outline-warning"
                              size="sm"
                              title="Modifier"
                            >
                              <FontAwesomeIcon icon={faEdit} />
                            </Button>
                            <Button 
                              variant="outline-danger"
                              size="sm"
                              title="Supprimer"
                              onClick={() => handleDelete(client.id)}
                            >
                              <FontAwesomeIcon icon={faTrashAlt} />
                            </Button>
                            
                            {/* Menu d'actions admin */}
                            {isAdmin && (
                              <Dropdown>
                                <Dropdown.Toggle 
                                  variant="outline-secondary" 
                                  size="sm"
                                  id={`dropdown-${client.id}`}
                                  title="Actions administrateur"
                                >
                                  <FontAwesomeIcon icon={faEllipsisV} />
                                </Dropdown.Toggle>

                                <Dropdown.Menu>
                                  {!client.accountValidated && (
                                    <>
                                      <Dropdown.Item
                                        className="text-success"
                                        onClick={() => handleValidateAccount(
                                          client.id, 
                                          `${client.prenom} ${client.nom}`.trim() || client.email
                                        )}
                                      >
                                        <FontAwesomeIcon icon={faCheckCircle} className="me-2" />
                                        Valider le compte
                                      </Dropdown.Item>
                                      <Dropdown.Divider />
                                    </>
                                  )}
                                  <Dropdown.Header>Changer le rôle</Dropdown.Header>
                                  {client.role !== 'CLIENT' && (
                                    <Dropdown.Item
                                      onClick={() => handleChangeRole(
                                        client.id, 
                                        `${client.prenom} ${client.nom}`.trim() || client.email,
                                        'CLIENT'
                                      )}
                                    >
                                      <FontAwesomeIcon icon={faUserTag} className="me-2" />
                                      Définir comme Client
                                    </Dropdown.Item>
                                  )}
                                  {client.role !== 'AGENT_SECURITE' && (
                                    <Dropdown.Item
                                      onClick={() => handleChangeRole(
                                        client.id, 
                                        `${client.prenom} ${client.nom}`.trim() || client.email,
                                        'AGENT_SECURITE'
                                      )}
                                    >
                                      <FontAwesomeIcon icon={faUserGear} className="me-2" />
                                      Définir comme Agent de Sécurité
                                    </Dropdown.Item>
                                  )}
                                  {client.role !== 'ADMIN' && (
                                    <Dropdown.Item
                                      className="text-danger"
                                      onClick={() => handleChangeRole(
                                        client.id, 
                                        `${client.prenom} ${client.nom}`.trim() || client.email,
                                        'ADMIN'
                                      )}
                                    >
                                      <FontAwesomeIcon icon={faUserShield} className="me-2" />
                                      Définir comme Admin
                                    </Dropdown.Item>
                                  )}
                                </Dropdown.Menu>
                              </Dropdown>
                            )}
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              </div>
              
              {/* Pagination */}
              <Row className="mt-3">
                <Col className="d-flex align-items-center">
                  <span className="text-muted">
                    Affichage de {clients.length} client(s) sur {totalItems} au total
                  </span>
                </Col>
                <Col md="auto">
                  <Pagination className="mb-0 pagination-sm">
                    {renderPaginationItems()}
                  </Pagination>
                </Col>
              </Row>
            </>
          )}
        </Card.Body>
      </Card>
    </Container>
  );
}
