import { useState } from "react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";

interface MenuItemCardProps {
  id: string;
  name: string;
  restaurant: string;
  restaurant_logo?: string;
  price: number;
  image: string;
  description?: string;
  calories?: number;
  tags?: string[];
  onAddToCart?: () => void;
}

export const MenuItemCard = ({
  name,
  restaurant,
  restaurant_logo,
  price,
  image,
  description,
  calories,
  tags,
  onAddToCart,
}: MenuItemCardProps) => {
  const [imageSrc, setImageSrc] = useState(image);
  
  const handleImageError = () => {
    setImageSrc("/default-food.png");
  };

  return (
    <Card className="overflow-hidden hover:shadow-xl transition-all group h-full flex flex-col">
      <div className="h-48 overflow-hidden bg-gray-100 flex-shrink-0">
        <img 
          src={imageSrc} 
          alt={name}
          onError={handleImageError}
          className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
        />
      </div>
      <CardContent className="p-4 flex flex-col flex-grow">
        <div className="flex items-start justify-between mb-2">
          <div className="flex-1 min-w-0">
            <h4 className="font-bold text-lg line-clamp-2">{name}</h4>
            <div className="flex items-center gap-2 mt-1">
              {restaurant_logo && (
                <img 
                  src={restaurant_logo} 
                  alt={restaurant}
                  className="w-5 h-5 rounded-full flex-shrink-0"
                />
              )}
              <span className="text-sm text-muted-foreground truncate">{restaurant}</span>
            </div>
          </div>
          <div className="font-bold text-lg text-primary ml-2 flex-shrink-0">₹{price}</div>
        </div>
        
        {description && (
          <p className="text-sm text-muted-foreground mb-3 line-clamp-2">{description}</p>
        )}
        
        <div className="flex items-center gap-2 mb-3 flex-wrap min-h-[28px]">
          {calories && (
            <Badge variant="outline" className="text-xs">
              {calories} cal
            </Badge>
          )}
          {tags?.map(tag => (
            <Badge key={tag} variant="secondary" className="text-xs">
              {tag}
            </Badge>
          ))}
        </div>
        
        <div className="mt-auto">
          <Button 
            variant="hero" 
            size="sm" 
            className="w-full"
            onClick={onAddToCart}
          >
            Add to Cart
          </Button>
        </div>
      </CardContent>
    </Card>
  );
};
