import React from "react";
import { useNavigate, useParams } from "react-router-dom";

const DeleteEntreprise = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const handleDelete = () => {
    console.log(`Entreprise avec ID ${id} supprimée`);
    navigate("/entreprises");
  };

  return (
    <div>
      <h2>Supprimer l'Entreprise</h2>
      <p>Es-tu sûr de vouloir supprimer cette entreprise ?</p>
      <button onClick={handleDelete}>Oui, supprimer</button>
      <button onClick={() => navigate("/entreprises")}>Annuler</button>
    </div>
  );
};

export default DeleteEntreprise;
