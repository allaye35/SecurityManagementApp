import React, { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

const DeleteAgent = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [isDeleted, setIsDeleted] = useState(false);

  const handleDelete = async () => {
    try {
      const response = await fetch(`/api/agents/${id}`, {
        method: 'DELETE',
      });
      if (response.ok) {
        setIsDeleted(true);
        setTimeout(() => {
          navigate('/agents');
        }, 2000);
      } else {
        alert('Erreur lors de la suppression de l\'agent');
      }
    } catch (error) {
      console.error('Erreur de suppression:', error);
      alert('Erreur de connexion au serveur');
    }
  };

  return (
    <div>
      {isDeleted ? (
        <h2>Agent supprimé avec succès!</h2>
      ) : (
        <>
          <h2>Êtes-vous sûr de vouloir supprimer cet agent?</h2>
          <button onClick={handleDelete}>Confirmer la suppression</button>
          <button onClick={() => navigate('/agents')}>Annuler</button>
        </>
      )}
    </div>
  );
};

export default DeleteAgent;
