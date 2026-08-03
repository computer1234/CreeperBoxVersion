#ifndef MATH_H
#define MATH_H
#include <complex>

struct Vec2 {

    float x,y;

    Vec2() { x = y = 0; }
    Vec2(float a, float b) : x(a), y(b) {}
    Vec2(int a, int b) : x((float)a), y((float)b) {}
    Vec2 &operator=(const Vec2 &copy) {
        x = copy.x;
        y = copy.y;
        return *this;
    }
    bool operator==(const Vec2 &o) const { return x == o.x && y == o.y; }
    bool operator!=(const Vec2 &o) const { return x != o.x || y != o.y; }

    Vec2 sub(float f) {
        return {x - f, y - f};
    }
    Vec2 sub(float ox, float oy) {
        return {x - ox, y - oy};
    }
    Vec2 div(float f) {
        return {x / f, y / f};
    }

    Vec2 div(const Vec2 &o) {
        return {x / o.x, y / o.y};
    }
    Vec2 mul(const Vec2 &o) {
        return {x * o.x, y * o.y};
    }
    Vec2 mul(float f) {
        return {x * f, y * f};
    }
    Vec2 sub(const Vec2 &o) const {
        return {x - o.x, y - o.y};
    }
    Vec2 add(const Vec2 &o) {
        return {x + o.x, y + o.y};
    }
    Vec2 add(float o) {
        return {x + o, y + o};
    }
    Vec2 add(float ox, float oy) {
        return {x + ox, y + oy};
    }
    float magnitude() const { return std::sqrt(squaredlen()); }
    float squaredlen() const { return x * x + y * y; }

    Vec2 normalized() {
        return div(magnitude());
    }

    Vec2 cross(){
        return {-y, x};
    }

    float dot(float ox, float oy) const { return x * ox + y * oy; }


    float dot(const Vec2 &o) const { return x * o.x + y * o.y; }

    Vec2 normAngles() {
        float x = this->x;
        float y = this->y;
        while (x > 90.f)
            x -= 180.0f;
        while (x < -90.f)
            x += 180.0f;

        while (y > 180.0f)
            y -= 360.0f;
        while (y < -180.0f)
            y += 360.0f;
        return Vec2(x, y);
    }

};


struct Vec3 {
    float x,y,z;

    Vec3() { x = y = z = 0; }
    Vec3(int a, int b, int c) : x((float)a), y((float)b), z((float)c) {}
    Vec3(double a, double b, double c) : x((float)a), y((float)b), z((float)c) {}
    Vec3(float a, float b, float c) : x(a), y(b), z(c) {}
    Vec3(float a, float b) : x(a), y(b), z(0) {}
    Vec3(const Vec2 &copy, float zP) {
        x = copy.x;
        y = copy.y;
        z = zP;
    }
    Vec3(const Vec3 &copy) {
        x = copy.x;
        y = copy.y;
        z = copy.z;
    }

    Vec3(float *v) : x(v[0]), y(v[1]), z(v[2]) {}

    inline bool operator==(const Vec3 &o) const { return x == o.x && y == o.y && z == o.z; };
    bool operator!=(const Vec3 &o) const { return x != o.x || y != o.y || z != o.z; };
    Vec3 operator-() const { return Vec3(-x, -y, -z); };
Vec3 mul(const Vec3 &o) const {
		return Vec3(x * o.x, y * o.y, z * o.z);
	};
	Vec3 mul(float f) const {
		return Vec3(x * f, y * f, z * f);
	};
	Vec3 mul(float x1, float y1, float z1) const {
		return Vec3(x * x1, y * y1, z * z1);
	};
	Vec3 div(float f) const {
		return Vec3(x / f, y / f, z / f);
	};
	Vec3 div(float x1, float y1, float z1) const {
		return Vec3(x / x1, y / y1, z / z1);
	};
	Vec3 div(const Vec3 &o) const {
		return Vec3(x / o.x, y / o.y, z / o.z);
	};
	Vec3 add(float f) const {
		return Vec3(x + f, y + f, z + f);
	};
	Vec3 add(float x1, float y1, float z1) const {
		return Vec3(x + x1, y + y1, z + z1);
	};
	Vec3 sub(float f) const {
		return Vec3(x - f, y - f, z - f);
	};

	Vec3 sub(float x1, float y1, float z1) const {
		return Vec3(x - x1, y - y1, z - z1);
	};

	Vec3 floor() const {
		return Vec3(floorf(x), floorf(y), floorf(z));
	};

	Vec3 add(const Vec3 &o) const {
		return Vec3(x + o.x, y + o.y, z + o.z);
	}
	Vec3 sub(const Vec3 &o) const {
		return Vec3(x - o.x, y - o.y, z - o.z);
	}

	float squaredlen() const { return x * x + y * y + z * z; }
	float squaredxzlen() const { return x * x + z * z; }

	Vec3 lerp(const Vec3& other, float tx, float ty, float tz) const {
		Vec3 ne;
		ne.x = x + tx * (other.x - x);
		ne.y = y + ty * (other.y - y);
		ne.z = z + tz * (other.z - z);
		return ne;
	}

	Vec3 lerp(const Vec3 other, float val) const {
		Vec3 ne;
		ne.x = x + val * (other.x - x);
		ne.y = y + val * (other.y - y);
		ne.z = z + val * (other.z - z);
		return ne;
	}

	Vec3 lerp(const Vec3 *other, float val) const {
		Vec3 ne;
		ne.x = x + val * (other->x - x);
		ne.y = y + val * (other->y - y);
		ne.z = z + val * (other->z - z);
		return ne;
	}

	Vec2 flatten() const {
		return Vec2(x, y);
	}

	float sqrxy() const { return x * x + y * y; }

	float dot(const Vec3 &o) const { return x * o.x + y * o.y + z * o.z; }
	float dotxy(const Vec3 &o) const { return x * o.x + y * o.y; }

	float magnitude() const { return sqrtf(squaredlen()); }

	Vec3 normalize() {
		return div(magnitude());
	}

	float dist(const Vec3 &e) const {
		return sub(e).magnitude();
	}

	float Get2DDist(const Vec3 &e) const {
		float dx = e.x - x, dy = e.y - y;
		return sqrtf(dx * dx + dy * dy);
	}

	float magnitudexy() const { return sqrtf(x * x + y * y); }
	float magnitudexz() const { return sqrtf(x * x + z * z); }

	Vec3 cross(const Vec3 &b) {
		return Vec3(y * b.z - z * b.y, z * b.x - x * b.z, x * b.y - y * b.x);
	}
	float cxy(const Vec3 &a) { return x * a.y - y * a.x; }
	Vec3 &operator=(const Vec3 &copy) {
		x = copy.x;
		y = copy.y;
		z = copy.z;

		return *this;
	}
};

struct Vec3i {
    int x, y, z;
    Vec3i() { x = y = z = 0; }
    Vec3i(int a, int b, int c) : x(a), y(b), z(c) {}
    Vec3i(int a, int b) : x(a), y(b), z(0) {}
    Vec3i(const Vec3i &copy) {
        x = copy.x;
        y = copy.y;
        z = copy.z;
    }

    Vec3i(const Vec3 &copy) {
        x = (int)copy.x;
        y = (int)copy.y;
        z = (int)copy.z;
    }


};



struct AABB {
    Vec3 lower;
    Vec3 upper;
    Vec2 size;

    AABB() {}
    AABB(Vec3 l, Vec3 h) : lower(l), upper(h){};
    AABB(const AABB &aabb) {
        lower = Vec3(aabb.lower);
        upper = Vec3(aabb.upper);
    }
    AABB(Vec3 lower, float width, float height, float eyeHeight) {
        lower = lower.sub(Vec3(width, eyeHeight * 2, width).div(2));
        this->lower = lower;
        this->upper = {lower.x + width, lower.y + height, lower.z + width};
    }


};


#endif
