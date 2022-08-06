import React, {useEffect, useState} from 'react';
import {useParams, useNavigate} from "react-router-dom";
import useAxios from "../../hooks/useAxios";
import Api from "../../api";
import Loading from "../Loading";
import {Alert, Button} from "react-bootstrap";
import EditPlacement from './EditPlacement';

export default function PlacementPage(){
    let { placementId } = useParams();
    const [placement, error, loading, axiosFetch] = useAxios();
    let navigate = useNavigate();


    const handleDelete = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'delete',
            url: `/placements/${placementId}`,
        }).then(() => navigate('/placements', {replace: true}));
    }

    const getPlacement = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: `/placements/${placementId}`,
        });
    }

    useEffect(() => {
        getPlacement();
    }, []);

    return (
        <>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && placement &&
                <article>
                    <h2>{placement.name}</h2>
                    <Button variant="danger" onClick={handleDelete}>Delete Placement</Button>
                    <EditPlacement placement={placement} />
                </article>
            }
        </>
    );
}