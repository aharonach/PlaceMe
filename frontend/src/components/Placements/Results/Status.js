import {useParams} from "react-router-dom";
import Checkmark from "../../General/Checkmark";
import Loading from "../../Loading";
import {useEffect, useState} from "react";
import Api from "../../../api";

const isCompleted = (status) => {
    return status === 'COMPLETED';
}

export default function Status({ placementResult, updateList }) {
    const { placementId } = useParams();
    const [status, setStatus] = useState(placementResult.status);

    const getResult = async () => {
        const res = await Api.get(`/placements/${placementId}/results/${placementResult.id}`);

        if ( res.status === 200 ) {
            return isCompleted(res.data.status);
        }

        return false;
    }

    useEffect(() => {
        if ( isCompleted(status) ) {
            return;
        }

        const interval = setInterval(() => {
            getResult().then( res => {
                if ( res ) {
                    setStatus('COMPLETED');
                    clearInterval(interval);
                    updateList();
                }
            });
        }, 1000);

        return () => clearInterval(interval);
    }, []);

    if ( isCompleted( status ) ) {
        return <Checkmark checked={true} />;
    }

    return <Loading block={false} show={true} size="sm" />;
}